package io.vgs.track.interceptor.transaction;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

import io.vgs.track.BaseTest;
import io.vgs.track.data.EntityTrackingData;
import io.vgs.track.meta.NotTracked;
import io.vgs.track.meta.Trackable;
import io.vgs.track.meta.Tracked;
import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.junit.jupiter.api.Test;

public class NotTrackedFieldTest extends BaseTest {

  @Test
  public void shouldNotTrackEntityFieldIfItDoesNotHaveTrackedAnnotation() {
    doInJpa(em -> {
      Client client = new Client();
      client.setName("name");
      client.setAge(27);
      em.persist(client);
    });

    List<EntityTrackingData> inserts = testEntityTrackingListener.getInserts();
    assertThat(inserts, hasSize(1));
  }

  @Test
  public void shouldNotTrackEntityFieldsIfTheyDontHaveTrackedAnnotation() {
    doInJpa(em -> {
      Account account = new Account();
      account.setAmount(300);
      em.persist(account);

      List<EntityTrackingData> inserts = testEntityTrackingListener.getInserts();
      assertThat(inserts, hasSize(0));
    });
  }

  @Test
  public void shouldNotTrackIfFieldsHaveNotTrackedAnnotation() {
    doInJpa(em -> {
      Passport passport = new Passport();
      passport.setNumber("123");
      passport.setCreatedDate(new Date());
      passport.setOwner("John");
      em.persist(passport);

      List<EntityTrackingData> inserts = testEntityTrackingListener.getInserts();
      assertThat(inserts, hasSize(0));
    });
  }

  @Override
  protected Class<?>[] entities() {
    return new Class<?>[]{
        Client.class,
        Account.class,
        Passport.class
    };
  }

  @Entity
  @Trackable
  @Tracked
  private static class Client {

    @Id
    @GeneratedValue
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

  @Entity
  @Trackable
  private static class Account {

    @Id
    @GeneratedValue
    private Long id;

    private int amount;

    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
    }

    public int getAmount() {
      return amount;
    }

    public void setAmount(int amount) {
      this.amount = amount;
    }
  }

  @Entity
  @Trackable
  @Tracked
  private static class Passport {

    @Id
    @GeneratedValue
    private Long id;

    @NotTracked
    private String number;
    @NotTracked
    private String owner;
    @NotTracked
    private Date createdDate;

    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
    }

    public String getNumber() {
      return number;
    }

    public void setNumber(String number) {
      this.number = number;
    }

    public String getOwner() {
      return owner;
    }

    public void setOwner(String owner) {
      this.owner = owner;
    }

    public Date getCreatedDate() {
      return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
      this.createdDate = createdDate;
    }
  }
}
