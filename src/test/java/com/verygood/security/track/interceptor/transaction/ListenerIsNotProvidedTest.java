package com.verygood.security.track.interceptor.transaction;

import com.verygood.security.track.BaseTest;
import com.verygood.security.track.exception.IllegalEntityTrackingInterceptorException;
import com.verygood.security.track.interceptor.EntityTrackingTransactionInterceptor;

import org.hibernate.Interceptor;
import org.junit.Test;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import static org.hibernate.testing.transaction.TransactionUtil.doInJPA;

public class ListenerIsNotProvidedTest extends BaseTest {

  @Test(expected = IllegalEntityTrackingInterceptorException.class)
  public void shouldFailIfListenerIsNotProvided() {
    doInJPA(this::entityManagerFactory, em -> {
      Client client = new Client();
      em.persist(client);
    });
  }

  @Override
  protected Interceptor interceptor() {
    return new EntityTrackingTransactionInterceptor();
  }

  @Override
  protected Class<?>[] entities() {
    return new Class<?>[]{
        Client.class
    };
  }

  @Entity
  private static class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_seq")
    @SequenceGenerator(name = "client_seq", sequenceName = "client_seq")
    private Long id;
  }
}
