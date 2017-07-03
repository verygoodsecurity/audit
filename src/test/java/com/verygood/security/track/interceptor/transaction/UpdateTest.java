package com.verygood.security.track.interceptor.transaction;

import com.verygood.security.track.data.EntityTrackingData;
import com.verygood.security.track.data.EntityTrackingFieldData;
import com.verygood.security.track.meta.Trackable;
import com.verygood.security.track.meta.Tracked;

import org.junit.Test;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import static com.verygood.security.track.sqltracker.AssertSqlCount.assertSqlCount;
import static com.verygood.security.track.sqltracker.AssertSqlCount.assertUpdateCount;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class UpdateTest extends BaseTransactionTest {

  @Test
  public void testSingleEntityUpdate() {
    // set up
    Serializable accountId = doInJPA(em -> {
      Account account = new Account();
      account.setFirst("old 1");
      account.setSecond("old 2");
      account.setThird("old 3");
      em.persist(account);
      return account.getId();
    });
    clearContext();

    doInJPA(em -> {
      Account account = em.find(Account.class, accountId);
      account.setFirst("new 1");
      account.setSecond("new 2");
      account.setThird("new 3");
      em.persist(account);
    });

    List<EntityTrackingData> updatedEntities = entityTrackingListener.getUpdates();
    assertThat(updatedEntities.size(), is(1));

    List<EntityTrackingFieldData> updatedFields = getUpdatedFields(Account.class);
    assertThat(updatedFields.size(), is(3));

    assertUpdateCount(1);
    assertSqlCount(2);
  }

  @Override
  protected Class<?>[] entities() {
    return new Class<?>[]{
        Account.class,
    };
  }

  @Entity
  @Trackable
  @Tracked
  private static class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_seq")
    @SequenceGenerator(name = "account_seq", sequenceName = "account_seq")
    private Long id;

    private String first;
    private String second;
    private String third;

    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
    }

    public String getFirst() {
      return first;
    }

    public void setFirst(String first) {
      this.first = first;
    }

    public String getSecond() {
      return second;
    }

    public void setSecond(String second) {
      this.second = second;
    }

    public String getThird() {
      return third;
    }

    public void setThird(String third) {
      this.third = third;
    }
  }

}
