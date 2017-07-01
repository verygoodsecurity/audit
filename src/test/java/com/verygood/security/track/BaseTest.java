package com.verygood.security.track;

import com.verygood.security.track.data.EntityTrackingData;
import com.verygood.security.track.data.EntityTrackingFieldData;
import com.verygood.security.track.listener.TestEntityStateEntityTrackingListener;
import com.verygood.security.track.sqltracker.QueryCountInfoHolder;
import com.verygood.security.track.sqltracker.SqlCountTrackerDatasource;

import org.h2.jdbcx.JdbcDataSource;
import org.hibernate.Interceptor;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Before;

import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import static java.util.stream.Collectors.toMap;

public abstract class BaseTest {
  private SessionFactory sf;
  private EntityManagerFactory emf;

  protected TestEntityStateEntityTrackingListener entityTrackingListener = new TestEntityStateEntityTrackingListener();

  @Before
  public void init() {
    sf = newSessionFactory();
    emf = sf.unwrap(EntityManagerFactory.class);
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

  private Properties getProperties() {
    Properties properties = new Properties();
    properties.put(AvailableSettings.DIALECT, "org.hibernate.dialect.H2Dialect");
    properties.put(AvailableSettings.HBM2DDL_AUTO, "create");
    properties.put(AvailableSettings.DATASOURCE, dataSource());
    properties.put(AvailableSettings.INTERCEPTOR, interceptor());
    properties.put(AvailableSettings.SHOW_SQL, false);
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

  protected abstract Interceptor interceptor();

  protected abstract Class<?>[] entities();

}
