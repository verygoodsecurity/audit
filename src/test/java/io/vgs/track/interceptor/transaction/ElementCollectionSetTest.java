package io.vgs.track.interceptor.transaction;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import io.vgs.track.BaseTest;
import io.vgs.track.meta.Trackable;
import io.vgs.track.meta.Tracked;

@SuppressWarnings("Duplicates")
public class ElementCollectionSetTest extends BaseTest {
  @Test
  public void simplePersistSave() {
    doInJpa(em -> {
      Client client = new Client();
      client.setNickNames(Sets.newHashSet("Flash", "Batman", "Thor"));
      em.persist(client);
    });
    System.out.println("INSERTS: ");
    testEntityTrackingListener.getInserts().forEach(System.out::println);
  }

  @Test
  public void changeAndFlush() {
    doInJpa(em -> {
      Client client = new Client();
      client.setNickNames(Sets.newHashSet("Flash", "Batman", "Thor"));
      em.persist(client);
      em.flush();
      client.addNickName("Iron Man");
    });
    System.out.println("INSERTS: ");
    testEntityTrackingListener.getInserts().forEach(System.out::println);
  }

  @Test
  public void createAndUpdate() {
    Long clientId = doInJpa(em -> {
      Client client = new Client();
      client.setNickNames(Sets.newHashSet("Flash", "Batman", "Thor"));
      em.persist(client);
      return client.getId();
    });

    doInJpa(em -> {
      Client client = em.find(Client.class, clientId);
      client.getNickNames().addAll(Lists.newArrayList("Hulk", "Iron Man"));
    });

    System.out.println("INSERTS: ");
    testEntityTrackingListener.getInserts().forEach(System.out::println);
    System.out.println("UPDATES: ");
    testEntityTrackingListener.getUpdates().forEach(System.out::println);
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

    public void addNickName(String nickName) {
      nickNames.add(nickName);
    }
  }


  @Override
  protected Class<?>[] entities() {
    return new Class<?>[]{
        Client.class
    };
  }
}
