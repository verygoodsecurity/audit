package io.vgs.track.interceptor.transaction;

import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import io.vgs.track.BaseTest;
import io.vgs.track.data.EntityTrackingData;
import io.vgs.track.interceptor.EntityTrackingTransactionInterceptor;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@Ignore
public class ListenerIsNotProvidedTest extends BaseTest {

  @Test
  public void shouldFailIfListenerIsNotProvided() {
    doInJpa(em -> {
      Client client = new Client();
      em.persist(client);
      List<EntityTrackingData> insertedEntities = testEntityTrackingListener.getInserts();
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
    @GeneratedValue
    private Long id;
  }
}
