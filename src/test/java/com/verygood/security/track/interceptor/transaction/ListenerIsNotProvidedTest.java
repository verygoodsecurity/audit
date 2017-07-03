package com.verygood.security.track.interceptor.transaction;

import com.verygood.security.track.BaseTest;
import com.verygood.security.track.data.EntityTrackingData;
import com.verygood.security.track.interceptor.EntityTrackingTransactionInterceptor;

import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@Ignore
public class ListenerIsNotProvidedTest extends BaseTest {

  @Test
  public void shouldFailIfListenerIsNotProvided() {
    doInJPA(em -> {
      Client client = new Client();
      em.persist(client);
      List<EntityTrackingData> insertedEntities = entityTrackingListener.getInserts();
      assertThat(insertedEntities.size(), is(0));
    });
  }

  @Override
  protected void addListener(EntityManager entityManager) {
    // NOP
  }

  @Override
  protected String interceptor() {
    return EntityTrackingTransactionInterceptor.class.getName();
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
