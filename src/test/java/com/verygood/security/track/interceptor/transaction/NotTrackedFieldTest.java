package com.verygood.security.track.interceptor.transaction;

import com.verygood.security.track.data.EntityTrackingData;
import com.verygood.security.track.meta.NotTracked;
import com.verygood.security.track.meta.Trackable;
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

public class NotTrackedFieldTest extends BaseTransactionTest {
  @Test
  public void shouldNotTrackEntityIfItDoesNotHaveTrackableAnnotation() {
    doInJPA(em -> {
      Client client = new Client();
      client.setName("name");
      client.setAge(27);
      em.persist(client);
    });

    List<EntityTrackingData> inserts = entityTrackingListener.getInserts();
    assertThat(inserts.size(), is(1));
  }

  @Entity
  @Trackable
  @Tracked
  private static class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_seq")
    @SequenceGenerator(name = "client_seq", sequenceName = "client_seq")
    private Long id;

    private String name;

    @NotTracked
    private int age;

    public int getAge() {
      return age;
    }

    public void setAge(int age) {
      this.age = age;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }
  }

  @Override
  protected Class<?>[] entities() {
    return new Class<?>[]{
        Client.class
    };
  }
}
