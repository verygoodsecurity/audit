package io.vgs.track;

import io.vgs.track.data.EntityTrackingData;
import io.vgs.track.data.EntityTrackingFieldData;
import io.vgs.track.interceptor.EntityTrackingListenerAware;
import io.vgs.track.listener.TestEntityStateEntityTrackingListener;
import io.vgs.track.sqltracker.QueryCountInfoHolder;
import io.vgs.track.sqltracker.SqlCountTrackerDatasource;

import org.h2.jdbcx.JdbcDataSource;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.spi.SessionImplementor;
import org.junit.After;
import org.junit.Before;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.sql.DataSource;

import static java.util.stream.Collectors.toMap;

public abstract class BaseTest {
  private SessionFactory sf;
  private EntityManagerFactory emf;

  protected TestEntityStateEntityTrackingListener entityTrackingListener = new TestEntityStateEntityTrackingListener();

  @Before
  public void init() {
    sf = newSessionFactory();
    emf = newEntityManagerFactory();
  }

  @After
  public void destroy() {
    clearContext();
    sf.close();
    emf.close();
  }

  protected void clearContext() {
    QueryCountInfoHolder.clear();
    entityTrackingListener.clear();
  }

  protected List<EntityTrackingFieldData> getInsertedFields(Class clazz) {
    return entityTrackingListener.getInserts().stream()
        .collect(toMap(EntityTrackingData::getClazz, EntityTrackingData::getEntityTrackingFields))
        .getOrDefault(clazz, Collections.emptyList());
  }

  protected List<EntityTrackingFieldData> getUpdatedFields(Class clazz) {
    return entityTrackingListener.getUpdates().stream()
        .collect(toMap(EntityTrackingData::getClazz, EntityTrackingData::getEntityTrackingFields))
        .getOrDefault(clazz, Collections.emptyList());
  }

  protected List<EntityTrackingFieldData> getDeletedFields(Class clazz) {
    return entityTrackingListener.getDeletes().stream()
        .collect(toMap(EntityTrackingData::getClazz, EntityTrackingData::getEntityTrackingFields))
        .getOrDefault(clazz, Collections.emptyList());
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
    properties.put(AvailableSettings.SHOW_SQL, true);
    properties.put(AvailableSettings.FORMAT_SQL, true);
    return properties;
  }

  private DataSource dataSource() {
    return new SqlCountTrackerDatasource(realDataSource());
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

  protected abstract String interceptor();

  protected abstract Class<?>[] entities();

  protected void addListener(EntityManager entityManager) {
    EntityTrackingListenerAware interceptor = (EntityTrackingListenerAware) ((SessionImplementor) entityManager.getDelegate()).getInterceptor();
    interceptor.setEntityTrackingListener(entityTrackingListener);
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
      if (txn != null && txn.isActive()) txn.rollback();
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
      if (txn != null && txn.isActive()) txn.rollback();
      throw e;
    } finally {
      if (entityManager != null) {
        entityManager.close();
      }
    }
  }

}
