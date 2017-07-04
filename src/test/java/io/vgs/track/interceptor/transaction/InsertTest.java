package io.vgs.track.interceptor.transaction;

import io.vgs.track.data.EntityTrackingData;
import io.vgs.track.data.EntityTrackingFieldData;
import io.vgs.track.meta.Trackable;
import io.vgs.track.meta.Tracked;
import io.vgs.track.sqltracker.AssertSqlCount;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class InsertTest extends BaseTransactionTest {

  @Test
  public void testSingleEntityInsertion() {
    doInJpa(em -> {
      Account account = new Account();
      account.setAmount(100);
      em.persist(account);
    });

    List<EntityTrackingData> insertedEntities = entityTrackingListener.getInserts();
    assertThat(insertedEntities.size(), is(1));

    List<EntityTrackingFieldData> insertedFields = getInsertedFields(Account.class);
    assertThat(insertedFields.size(), is(1));

    AssertSqlCount.assertInsertCount(1);
    AssertSqlCount.assertSqlCount(1);
  }

  @Test
  public void testOneToManyInsertion() {
    doInJpa(em -> {
      Address address = new Address();
      address.setCity("Seattle");
      address.setStreet("street");

      Car car = new Car();
      car.setColor("green");

      address.getCars().add(car);
      car.setAddress(address);

      em.persist(address);
      em.persist(car);
    });

    List<EntityTrackingData> insertedEntities = entityTrackingListener.getInserts();
    assertThat(insertedEntities.size(), is(1));

    List<EntityTrackingFieldData> insertedCarFields = getInsertedFields(Car.class);
    assertThat(insertedCarFields.size(), is(2));

    AssertSqlCount.assertInsertCount(2);
    AssertSqlCount.assertSqlCount(2);
  }

  @Test
  public void testOneToOneInsertion1() {
    doInJpa(em -> {
      Address address = new Address();
      Client client = new Client();

      client.setAddress(address);
      address.setClient(client);

      em.persist(client);
      em.persist(address);
    });

    List<EntityTrackingData> inserts = entityTrackingListener.getInserts();
    assertThat(inserts.size(), is(1));

    List<EntityTrackingFieldData> addressInsertedFields = getInsertedFields(Address.class);
    List<EntityTrackingFieldData> clientInsertedFields = getInsertedFields(Client.class);
    assertThat(addressInsertedFields.size(), is(1));
    assertThat(clientInsertedFields.size(), is(0));

    AssertSqlCount.assertInsertCount(2);
    AssertSqlCount.assertSqlCount(2);
  }

  @Test
  public void testOneToOneInsertion2() {
    doInJpa(em -> {
      Client client = new Client();
      Passport passport = new Passport();

      client.setPassport(passport);
      passport.setClient(client);

      em.persist(passport);
      em.persist(client);
    });

    List<EntityTrackingData> inserts = entityTrackingListener.getInserts();
    assertThat(inserts.size(), is(1));

    List<EntityTrackingFieldData> clientInsertedFields = getInsertedFields(Client.class);
    assertThat(clientInsertedFields.size(), is(1));

    AssertSqlCount.assertInsertCount(2);
    AssertSqlCount.assertSqlCount(2);
  }

  @Override
  protected Class<?>[] entities() {
    return new Class<?>[]{
        Client.class,
        Account.class,
        Car.class,
        Address.class,
        Passport.class
    };
  }

  @Entity
  @Trackable
  private static class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_seq")
    @SequenceGenerator(name = "client_seq", sequenceName = "client_seq")
    private Long id;

    @OneToOne(mappedBy = "client", fetch = FetchType.LAZY)
    @Tracked
    private Address address;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_passport")
    @Tracked
    private Passport passport;

    public Passport getPassport() {
      return passport;
    }

    public void setPassport(Passport passport) {
      this.passport = passport;
    }

    public Address getAddress() {
      return address;
    }

    public void setAddress(Address address) {
      this.address = address;
    }
  }

  @Entity
  @Trackable
  @Tracked
  private static class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_seq")
    @SequenceGenerator(name = "account_seq", sequenceName = "account_seq")
    private Long id;

    private Integer amount;

    @JoinColumn(name = "id_client")
    @ManyToOne(fetch = FetchType.LAZY)
    private Client client;

    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
    }

    public Integer getAmount() {
      return amount;
    }

    public void setAmount(Integer amount) {
      this.amount = amount;
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
  private static class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "car_seq")
    @SequenceGenerator(name = "car_seq", sequenceName = "car_seq")
    private Long id;

    private String color;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_address")
    private Address address;

    public Address getAddress() {
      return address;
    }

    public void setAddress(Address address) {
      this.address = address;
    }

    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
    }

    public String getColor() {
      return color;
    }

    public void setColor(String color) {
      this.color = color;
    }
  }

  @Entity
  @Trackable
  private static class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "address_seq")
    @SequenceGenerator(name = "address_seq", sequenceName = "address_seq")
    private Long id;

    private String street;
    private String city;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_client")
    @Tracked
    private Client client;

    @OneToMany(mappedBy = "address")
    private List<Car> cars = new ArrayList<>();

    public List<Car> getCars() {
      return cars;
    }

    public void setCars(List<Car> cars) {
      this.cars = cars;
    }

    public String getStreet() {
      return street;
    }

    public void setStreet(String street) {
      this.street = street;
    }

    public Client getClient() {
      return client;
    }

    public void setClient(Client client) {
      this.client = client;
    }

    public void setCity(String city) {
      this.city = city;
    }

    public String getCity() {
      return city;
    }
  }

  @Entity
  @Trackable
  @Tracked
  private static class Passport {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "address_seq")
    @SequenceGenerator(name = "address_seq", sequenceName = "address_seq")
    private Long id;

    @OneToOne(mappedBy = "passport", fetch = FetchType.LAZY)
    private Client client;

    public Client getClient() {
      return client;
    }

    public void setClient(Client client) {
      this.client = client;
    }
  }


}
