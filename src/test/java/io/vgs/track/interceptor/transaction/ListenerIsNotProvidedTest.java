package io.vgs.track.interceptor.transaction;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

import io.vgs.track.BaseTest;
import io.vgs.track.data.EntityTrackingData;
import io.vgs.track.meta.Trackable;
import io.vgs.track.meta.Tracked;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.junit.jupiter.api.Test;

public class ListenerIsNotProvidedTest extends BaseTest {

  @Test
  public void shouldNotFailIfListenerIsNotProvided() {
    doInJpa(em -> {
      Client client = new Client();
      client.setName("test");
      em.persist(client);
      List<EntityTrackingData> insertedEntities = testEntityTrackingListener.getInserts();
      assertThat(insertedEntities, hasSize(0));
    });
  }

  @Override
  protected void addListener(EntityManager entityManager) {
    // NOP
  }

  @Override
  protected Class<?>[] entities() {
    return new Class<?>[]{
        Client.class
    };
  }

  @Entity
  @Tracked
  @Trackable
  private static class Client {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }
  }
}
