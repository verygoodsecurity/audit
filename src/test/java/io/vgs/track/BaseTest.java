package io.vgs.track;

import com.p6spy.engine.spy.P6DataSource;
import io.vgs.track.interceptor.EntityTrackingListenerAware;
import io.vgs.track.interceptor.EntityTrackingTransactionInterceptor;
import io.vgs.track.listener.TestEntityStateEntityTrackingListener;
import io.vgs.track.sqltracker.QueryCountInfoHolder;
import io.vgs.track.sqltracker.SqlCountTrackerDatasource;
import java.util.Arrays;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.sql.DataSource;
import org.h2.jdbcx.JdbcDataSource;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.spi.SessionImplementor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public abstract class BaseTest {

  private SessionFactory sf;
  private EntityManagerFactory emf;

  protected TestEntityStateEntityTrackingListener testEntityTrackingListener = new TestEntityStateEntityTrackingListener();

  @BeforeEach
  public void init() {
    sf = newSessionFactory();
    emf = newEntityManagerFactory();
  }

  @AfterEach
  public void destroy() {
    clearContext();
    sf.close();
    emf.close();
  }

  protected void clearContext() {
    QueryCountInfoHolder.clear();
    testEntityTrackingListener.clear();
  }

  private SessionFactory newSessionFactory() {
    Properties properties = getProperties();
    Configuration configuration = new Configuration().addProperties(properties);
    for (Class<?> entityClass : entities()) {
      configuration.addAnnotatedClass(entityClass);
    }
    return configuration.buildSessionFactory(
        new StandardServiceRegistryBuilder()
            .applySettings(properties)
            .build()
    );
  }

  private EntityManagerFactory newEntityManagerFactory() {
    Properties properties = getProperties();
    properties.put(org.hibernate.jpa.AvailableSettings.LOADED_CLASSES, Arrays.asList(entities()));
    return Persistence.createEntityManagerFactory("track", properties);
  }

  private Properties getProperties() {
    Properties properties = new Properties();
    properties.put(AvailableSettings.DIALECT, "org.hibernate.dialect.H2Dialect");
    properties.put(AvailableSettings.HBM2DDL_AUTO, "create");
    properties.put(AvailableSettings.DATASOURCE, dataSource());
    properties.put(org.hibernate.jpa.AvailableSettings.SESSION_INTERCEPTOR, interceptor());
    properties.put(AvailableSettings.SHOW_SQL, false);
    properties.put(AvailableSettings.FORMAT_SQL, true);
    return properties;
  }

  private DataSource dataSource() {
    return new SqlCountTrackerDatasource(
        new P6DataSource(
            realDataSource()
        )
    );
  }

  private DataSource realDataSource() {
    JdbcDataSource dataSource = new JdbcDataSource();
    dataSource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
    dataSource.setUser("sa");
    dataSource.setPassword("");
    return dataSource;
  }

  protected EntityManagerFactory entityManagerFactory() {
    return emf;
  }

  protected String interceptor() {
    return EntityTrackingTransactionInterceptor.class.getName();
  }

  protected abstract Class<?>[] entities();

  protected void addListener(EntityManager entityManager) {
    Session session = (Session) entityManager.getDelegate();
    EntityTrackingListenerAware interceptor = (EntityTrackingListenerAware) ((SessionImplementor) session).getInterceptor();
    interceptor.setEntityTrackingListener(testEntityTrackingListener);
  }

  protected <T> T doInJpa(Function<EntityManager, T> function) {
    T result;
    EntityManager entityManager = null;
    EntityTransaction txn = null;
    try {
      entityManager = emf.createEntityManager();
      addListener(entityManager);
      txn = entityManager.getTransaction();
      txn.begin();
      result = function.apply(entityManager);
      txn.commit();
    } catch (RuntimeException e) {
      if (txn != null && txn.isActive()) {
        txn.rollback();
      }
      throw e;
    } finally {
      if (entityManager != null) {
        entityManager.close();
      }
    }
    return result;
  }

  protected void doInJpa(Consumer<EntityManager> function) {
    EntityManager entityManager = null;
    EntityTransaction txn = null;
    try {
      entityManager = emf.createEntityManager();
      addListener(entityManager);
      txn = entityManager.getTransaction();
      txn.begin();
      function.accept(entityManager);
      txn.commit();
    } catch (RuntimeException e) {
      if (txn != null && txn.isActive()) {
        txn.rollback();
      }
      throw e;
    } finally {
      if (entityManager != null) {
        entityManager.close();
      }
    }
  }

}
