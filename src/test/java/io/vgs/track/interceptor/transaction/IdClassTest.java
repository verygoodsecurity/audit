package io.vgs.track.interceptor.transaction;

import org.junit.Test;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import io.vgs.track.BaseTest;
import io.vgs.track.data.EntityTrackingData;
import io.vgs.track.meta.Trackable;
import io.vgs.track.meta.Tracked;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class IdClassTest extends BaseTest {

  @Test
  public void shouldSupportIdClass() {
    doInJpa(em -> {
      User user = new User();
      user.setName("John");
      Account account = new Account();
      account.setNumber("BD13553");

      UserAccount userAccount = new UserAccount();
      userAccount.setUser(user);
      userAccount.setAccount(account);

      em.persist(user);
      em.persist(account);
      em.persist(userAccount);
    });

    List<EntityTrackingData> inserts = testEntityTrackingListener.getInserts();
    assertThat(inserts.size(), is(2));
  }

  @Entity
  @Tracked
  @Trackable
  private static class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "user_seq")
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

  @Entity
  @Trackable
  @Tracked
  private static class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_seq")
    @SequenceGenerator(name = "account_seq", sequenceName = "account_seq")
    private Long id;
    private String number;

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
  }

  @Entity
  @IdClass(UserAccountPK.class)
  @Trackable
  @Tracked
  private static class UserAccount {
    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name = "account_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Account account;

    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private User user;

    public Account getAccount() {
      return account;
    }

    public void setAccount(Account account) {
      this.account = account;
    }

    public User getUser() {
      return user;
    }

    public void setUser(User user) {
      this.user = user;
    }
  }

  private static class UserAccountPK implements Serializable {
    private User user;
    private Account account;

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      UserAccountPK that = (UserAccountPK) o;

      if (user != null ? !user.equals(that.user) : that.user != null) return false;
      return account != null ? account.equals(that.account) : that.account == null;
    }

    @Override
    public int hashCode() {
      int result = user != null ? user.hashCode() : 0;
      result = 31 * result + (account != null ? account.hashCode() : 0);
      return result;
    }

    public User getUser() {
      return user;
    }

    public void setUser(User user) {
      this.user = user;
    }

    public Account getAccount() {
      return account;
    }

    public void setAccount(Account account) {
      this.account = account;
    }
  }


  @Override
  protected Class<?>[] entities() {
    return new Class[]{
        User.class,
        Account.class,
        UserAccount.class
    };
  }
}
