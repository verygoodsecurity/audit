package com.verygood.security.track;

import com.verygood.security.track.entity.Account;
import com.verygood.security.track.entity.Address;
import com.verygood.security.track.entity.Client;
import com.verygood.security.track.sqltracker.SqlCountTrackerDatasource;

import org.h2.jdbcx.JdbcDataSource;
import org.hibernate.Interceptor;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Before;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

public abstract class BaseTest {
  SessionFactory sf;
  EntityManagerFactory emf;

  @Before
  public void init() {
    sf = newSessionFactory();
    emf = sf.unwrap(EntityManagerFactory.class);
  }

  @After
  public void destroy() {
    sf.close();
    emf.close();
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

  private Interceptor interceptor() {
    TrackingEntityStateChangesInterceptor interceptor = new TrackingEntityStateChangesInterceptor();
    interceptor.setTestEntityStateTrackReporter(TestTestEntityStateTrackReporter.getInstance());
    return interceptor;
  }

  private Class<?>[] entities() {
    return new Class<?>[]{
        Client.class,
        Account.class,
        Address.class
    };
  }

  private Properties getProperties() {
    Properties properties = new Properties();
    properties.put(AvailableSettings.DIALECT, "org.hibernate.dialect.H2Dialect");
    properties.put(AvailableSettings.HBM2DDL_AUTO, "create");
    properties.put(AvailableSettings.DATASOURCE, dataSource());
    properties.put(AvailableSettings.INTERCEPTOR, interceptor());
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
}
