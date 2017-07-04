package com.verygood.security.track.interceptor.transaction;

import com.verygood.security.track.data.EntityTrackingData;
import com.verygood.security.track.meta.Tracked;

import org.junit.Test;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class NotTrackableTest extends BaseTransactionTest {

  @Test
  public void shouldNotTrackEntityIfItDoesNotHaveTrackableAnnotation() {
    doInJpa(em -> {
      Client client = new Client();
      em.persist(client);
    });

    List<EntityTrackingData> inserts = entityTrackingListener.getInserts();
    assertThat(inserts.size(), is(0));
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
