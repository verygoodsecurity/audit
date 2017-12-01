package io.vgs.track.interceptor.transaction;

import org.junit.Test;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import io.vgs.track.BaseTest;
import io.vgs.track.data.EntityTrackingData;
import io.vgs.track.data.EntityTrackingFieldData;
import io.vgs.track.meta.Trackable;
import io.vgs.track.meta.Tracked;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

@SuppressWarnings("Duplicates")
public class TrackPrimitiveFieldTest extends BaseTest {

  @Test
  public void shouldTrackPrimitiveInsertion() {
    doInJpa(em -> {
      Account account = new Account();
      account.setAmount(100);
      em.persist(account);
    });

    List<EntityTrackingData> insertedEntities = testEntityTrackingListener.getInserts();
    assertThat(insertedEntities.size(), is(1));

    List<EntityTrackingFieldData> insertedFields = testEntityTrackingListener.getInsertedFields(Account.class);
    assertThat(insertedFields.size(), is(1));

    EntityTrackingFieldData insertedField = testEntityTrackingListener.getInsertedField("amount");
    assertThat(insertedField.getOldValue(), is(nullValue()));
    assertThat(insertedField.getNewValue(), is(100));
  }

  @Test
  public void shouldTrackPrimitiveUpdate() {
    Long accountId = doInJpa(em -> {
      Account account = new Account();
      account.setAmount(10);
      em.persist(account);
      return account.getId();
    });

    clearContext();

    doInJpa(em -> {
      Account account = em.find(Account.class, accountId);
      account.setAmount(20);
    });

    assertThat(testEntityTrackingListener.getInserts().isEmpty(), is(true));
    List<EntityTrackingData> updates = testEntityTrackingListener.getUpdates();
    assertThat(updates.size(), is(1));
    EntityTrackingFieldData amount = testEntityTrackingListener.getUpdatedField("amount");
    assertThat(amount.getOldValue(), is(10));
    assertThat(amount.getNewValue(), is(20));
  }

  @Test
  public void shouldTrackSingleDeletion() {
    Long accountId = doInJpa(em -> {
      Account account = new Account();
      account.setAmount(10);
      em.persist(account);
      return account.getId();
    });

    doInJpa(em -> {
      em.remove(em.find(Account.class, accountId));
    });

    List<EntityTrackingData> deletes = testEntityTrackingListener.getDeletes();
    assertThat(deletes.size(), is(1));
    EntityTrackingFieldData amount = testEntityTrackingListener.getDeletedField("amount");
    assertThat(amount.getNewValue(), is(nullValue()));
    assertThat(amount.getOldValue(), is(10));
  }

  @Test
  public void shouldTrackSingleChangeWhenPrimitiveFieldChangingManyTimes() {
    doInJpa(em -> {
      Account account = new Account();
      account.setAmount(10);
      em.persist(account);
      em.flush();

      account.setAmount(20);
      em.flush();

      account.setAmount(30);
      em.flush();

      account.setAmount(40);
      em.flush();
    });
    assertThat(testEntityTrackingListener.getUpdates().isEmpty(), is(true));
    List<EntityTrackingData> inserts = testEntityTrackingListener.getInserts();
    assertThat(inserts.size(), is(1));
    EntityTrackingFieldData amount = testEntityTrackingListener.getInsertedField("amount");
    assertThat(amount.getOldValue(), is(nullValue()));
    assertThat(amount.getNewValue(), is(40));

  }

  @Override
  protected Class<?>[] entities() {
    return new Class[]{
        Account.class
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

    private Integer amount;

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
  }
}
