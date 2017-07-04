package com.verygood.security.track.interceptor.transaction;

import com.verygood.security.track.data.EntityTrackingData;
import com.verygood.security.track.meta.NotTracked;
import com.verygood.security.track.meta.Trackable;
import com.verygood.security.track.meta.Tracked;

import org.junit.Test;

import java.util.Date;
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
  public void shouldNotTrackEntityFieldIfItDoesNotHaveTrackedAnnotation() {
    doInJpa(em -> {
      Client client = new Client();
      client.setName("name");
      client.setAge(27);
      em.persist(client);
    });

    List<EntityTrackingData> inserts = entityTrackingListener.getInserts();
    assertThat(inserts.size(), is(1));
  }

  @Test
  public void shouldNotTrackEntityFieldsIfTheyDontHaveTrackedAnnotation() {
    doInJpa(em -> {
      Account account = new Account();
      account.setAmount(300);
      em.persist(account);

      List<EntityTrackingData> inserts = entityTrackingListener.getInserts();
      assertThat(inserts.size(), is(0));
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

      List<EntityTrackingData> inserts = entityTrackingListener.getInserts();
      assertThat(inserts.size(), is(0));
    });
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
