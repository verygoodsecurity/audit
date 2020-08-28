package io.vgs.track.interceptor.transaction;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

import io.vgs.track.BaseTest;
import io.vgs.track.data.EntityTrackingData;
import io.vgs.track.data.EntityTrackingFieldData;
import io.vgs.track.meta.Trackable;
import io.vgs.track.meta.Tracked;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("Duplicates")
public class OneToOneUnidirectionalTest extends BaseTest {

  @Test
  public void insertCorrectOrder() {
    doInJpa(em -> {
      Address address = new Address();
      Client client = new Client();

      client.setAddress(address);

      em.persist(address);
      em.persist(client);
    });

    List<EntityTrackingData> inserts = testEntityTrackingListener.getInserts();
    assertThat(inserts, hasSize(1));
    EntityTrackingFieldData client = testEntityTrackingListener.getInsertedField("address");
    assertThat(client.getOldValue(), is(nullValue()));
    assertThat(client.getNewValue(), is(1L));
  }

  @Test
  public void insertWrongOrder() {
    doInJpa(em -> {
      Address address = new Address();
      Client client = new Client();

      client.setAddress(address);

      em.persist(client);
      em.persist(address);
    });

    List<EntityTrackingData> updates = testEntityTrackingListener.getUpdates();
    assertThat(updates, hasSize(1));
    EntityTrackingFieldData address = testEntityTrackingListener.getUpdatedField("address");
    assertThat(address.getOldValue(), is(nullValue()));
    assertThat(address.getNewValue(), is(1L));
  }

  @Test
  public void update() {
    Long clientId = doInJpa(em -> {
      Address address = new Address();
      Client client = new Client();

      client.setAddress(address);

      em.persist(client);
      em.persist(address);
      return client.getId();
    });

    Long newAddressId = doInJpa(em -> {
      Address newAddress = new Address();
      em.persist(newAddress);
      return newAddress.getId();
    });
    clearContext();

    doInJpa(em -> {
      Client client = em.find(Client.class, clientId);
      Address newAddress = em.find(Address.class, newAddressId);
      client.setAddress(newAddress);
    });

    List<EntityTrackingData> updates = testEntityTrackingListener.getUpdates();
    assertThat(updates, hasSize(1));
    EntityTrackingFieldData address = testEntityTrackingListener.getUpdatedField("address");
    assertThat(address.getOldValue(), is(1L));
    assertThat(address.getNewValue(), is(2L));
  }

  @Entity
  @Trackable
  @Tracked
  public static class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_seq")
    @SequenceGenerator(name = "client_seq", sequenceName = "client_seq")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private Address address;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_passport")
    @Tracked
    private Passport passport;

    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
    }

    public Address getAddress() {
      return address;
    }

    public void setAddress(Address address) {
      this.address = address;
    }

    public Passport getPassport() {
      return passport;
    }

    public void setPassport(Passport passport) {
      this.passport = passport;
    }
  }

  @Entity
  @Trackable
  @Tracked
  public static class Passport {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "address_seq")
    @SequenceGenerator(name = "address_seq", sequenceName = "address_seq")
    private Long id;

    @OneToOne(mappedBy = "passport", fetch = FetchType.LAZY)
    private Client client;

    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
    }

    public Client getClient() {
      return client;
    }

    public void setClient(Client client) {
      this.client = client;
    }
  }

  @Entity
  @Trackable
  @Tracked
  public static class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "address_seq")
    @SequenceGenerator(name = "address_seq", sequenceName = "address_seq")
    private Long id;

    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
    }

  }


  @Override
  protected Class<?>[] entities() {
    return new Class[]{
        Client.class,
        Passport.class,
        Address.class
    };
  }

}
