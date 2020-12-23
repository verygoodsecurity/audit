package io.vgs.track.interceptor.transaction;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;

import com.google.common.collect.Sets;
import io.vgs.track.BaseTest;
import io.vgs.track.data.EntityTrackingFieldData;
import io.vgs.track.meta.Trackable;
import io.vgs.track.meta.Tracked;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("Duplicates")
public class ElementCollectionSetTest extends BaseTest {

  @Test
  public void save() {
    Set<String> names = Sets.newHashSet("Flash", "Batman", "Thor");

    doInJpa(em -> {
      Client client = new Client();
      client.setNickNames(names);
      em.persist(client);
    });

    assertThat(testEntityTrackingListener.getInserts().size(), is(1));
    EntityTrackingFieldData nickNames = testEntityTrackingListener.getInsertedField("nickNames");
    assertThat(nickNames.getOldValue(), is(nullValue()));
    Collection<String> actualNickNames = (Collection<String>) nickNames.getNewValue();
    assertThat(actualNickNames, hasSize(3));
    assertThat(actualNickNames, containsInAnyOrder("Flash", "Batman", "Thor"));
  }

  @Test
  public void changeAndFlush() {
    doInJpa(em -> {
      Client client = new Client();
      client.setNickNames(Sets.newHashSet("Flash", "Batman", "Thor"));
      em.persist(client);
      em.flush();
      client.getNickNames().add("Iron Man");
    });
    assertThat(testEntityTrackingListener.getInserts().size(), is(1));
    EntityTrackingFieldData nickNames = testEntityTrackingListener.getInsertedField("nickNames");
    assertThat(nickNames.getOldValue(), is(nullValue()));
    Collection<String> actualNickNames = (Collection<String>) nickNames.getNewValue();
    assertThat(actualNickNames, hasSize(4));
    assertThat(actualNickNames, containsInAnyOrder("Flash", "Batman", "Thor", "Iron Man"));
  }


  @Test
  public void update() {
    Long clientId = doInJpa(em -> {
      Client client = new Client();
      client.setNickNames(Sets.newHashSet("Flash", "Batman", "Thor"));
      em.persist(client);
      return client.getId();
    });
    clearContext();

    doInJpa(em -> {
      Client client = em.find(Client.class, clientId);
      client.getNickNames().addAll(Sets.newHashSet("Hulk", "Iron Man"));
    });

    assertThat(testEntityTrackingListener.getUpdates().size(), is(1));
    EntityTrackingFieldData nickNames = testEntityTrackingListener.getUpdatedField("nickNames");
    Collection<String> actualOldValue = (Collection<String>) nickNames.getOldValue();
    Collection<String> actualNewValue = (Collection<String>) nickNames.getNewValue();

    assertThat(actualOldValue, hasSize(3));
    assertThat(actualOldValue, containsInAnyOrder("Flash", "Batman", "Thor"));

    assertThat(actualNewValue, hasSize(5));
    assertThat(actualNewValue, containsInAnyOrder("Flash", "Batman", "Thor", "Iron Man", "Hulk"));
  }

  @Override
  protected Class<?>[] entities() {
    return new Class<?>[]{
        Client.class
    };
  }

  @Entity
  @Trackable
  public static class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_seq")
    @SequenceGenerator(name = "client_seq", sequenceName = "client_seq")
    private Long id;

    @ElementCollection
    @Tracked
    private Set<String> nickNames = new HashSet<>();

    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
    }

    public Set<String> getNickNames() {
      return nickNames;
    }

    public void setNickNames(Set<String> nickNames) {
      this.nickNames = nickNames;
    }

  }
}
