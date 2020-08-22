package io.vgs.track.interceptor.transaction;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

import io.vgs.track.BaseTest;
import io.vgs.track.data.EntityTrackingData;
import io.vgs.track.meta.Tracked;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.junit.jupiter.api.Test;

public class NotTrackableTest extends BaseTest {

  @Test
  public void shouldNotTrackEntityIfItDoesNotHaveTrackableAnnotation() {
    doInJpa(em -> {
      Client client = new Client();
      em.persist(client);
    });

    List<EntityTrackingData> inserts = testEntityTrackingListener.getInserts();
    assertThat(inserts, hasSize(0));
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

    @Tracked
    private String name;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }
  }
}
