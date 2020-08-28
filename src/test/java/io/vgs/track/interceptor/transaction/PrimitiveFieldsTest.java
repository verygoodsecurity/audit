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
import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("Duplicates")
public class PrimitiveFieldsTest extends BaseTest {

  @Test
  public void shouldTrackPrimitiveInsertion() {
    doInJpa(em -> {
      Account account = new Account();
      account.setAmount(100);
      em.persist(account);
    });

    List<EntityTrackingData> insertedEntities = testEntityTrackingListener.getInserts();
    assertThat(insertedEntities, hasSize(1));

    List<EntityTrackingFieldData> insertedFields = testEntityTrackingListener.getInsertedFields(Account.class);
    assertThat(insertedFields, hasSize(1));

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
    assertThat(updates, hasSize(1));
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
    assertThat(deletes, hasSize(1));
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
    assertThat(inserts, hasSize(1));
    EntityTrackingFieldData amount = testEntityTrackingListener.getInsertedField("amount");
    assertThat(amount.getOldValue(), is(nullValue()));
    assertThat(amount.getNewValue(), is(40));
  }

  @Test
  public void createManyFields() {
    doInJpa(em -> {
      SimpleEntity entity = new SimpleEntity();
      entity.setFieldNumber1("old 1");
      entity.setFieldNumber2(2);
      entity.setFieldNumber3(3);
      em.persist(entity);
    });

    List<EntityTrackingData> inserts = testEntityTrackingListener.getInserts();
    assertThat(inserts, hasSize(1));
    EntityTrackingFieldData fieldNumber1 = testEntityTrackingListener.getInsertedField("fieldNumber1");
    assertThat(fieldNumber1.getOldValue(), is(nullValue()));
    assertThat(fieldNumber1.getNewValue(), is("old 1"));
    EntityTrackingFieldData fieldNumber2 = testEntityTrackingListener.getInsertedField("fieldNumber2");
    assertThat(fieldNumber2.getOldValue(), is(nullValue()));
    assertThat(fieldNumber2.getNewValue(), is(2));
    EntityTrackingFieldData fieldNumber3 = testEntityTrackingListener.getInsertedField("fieldNumber3");
    assertThat(fieldNumber3.getOldValue(), is(nullValue()));
    assertThat(fieldNumber3.getNewValue(), is(3));
  }

  @Test
  public void saveFieldWithDefaultValue() {
    doInJpa(em -> {
      SimpleEntity entity = new SimpleEntity();
      entity.setFieldWithDefaultValue(100);
      em.persist(entity);
    });

    EntityTrackingFieldData fieldWithDefaultValue = testEntityTrackingListener.getInsertedField("fieldWithDefaultValue");
    assertThat(fieldWithDefaultValue.getOldValue(), is(nullValue()));
    assertThat(fieldWithDefaultValue.getNewValue(), is(100));
  }

  @Test
  public void updateManyFields() {
    Serializable entityId = doInJpa(em -> {
      SimpleEntity entity = new SimpleEntity();
      entity.setFieldNumber1("old 1");
      entity.setFieldNumber2(1);
      entity.setFieldNumber3(1);
      em.persist(entity);
      return entity.getId();
    });
    clearContext();

    doInJpa(em -> {
      SimpleEntity entity = em.find(SimpleEntity.class, entityId);
      entity.setFieldNumber1("new 1");
      entity.setFieldNumber2(2);
      entity.setFieldNumber3(2);
      em.persist(entity);
    });

    List<EntityTrackingData> updates = testEntityTrackingListener.getUpdates();
    assertThat(updates, hasSize(1));
    EntityTrackingFieldData fieldNumber1 = testEntityTrackingListener.getUpdatedField("fieldNumber1");
    assertThat(fieldNumber1.getOldValue(), is("old 1"));
    assertThat(fieldNumber1.getNewValue(), is("new 1"));
    EntityTrackingFieldData fieldNumber2 = testEntityTrackingListener.getUpdatedField("fieldNumber2");
    assertThat(fieldNumber2.getOldValue(), is(1));
    assertThat(fieldNumber2.getNewValue(), is(2));
    EntityTrackingFieldData fieldNumber3 = testEntityTrackingListener.getUpdatedField("fieldNumber3");
    assertThat(fieldNumber3.getOldValue(), is(1));
    assertThat(fieldNumber3.getNewValue(), is(2));
  }

  @Override
  protected Class<?>[] entities() {
    return new Class[]{
        Account.class,
        SimpleEntity.class
    };
  }

  @Entity
  @Trackable
  @Tracked
  public static class Account {

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

  @Entity
  @Trackable
  @Tracked
  public static class SimpleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_seq")
    @SequenceGenerator(name = "account_seq", sequenceName = "account_seq")
    private Long id;

    private String fieldNumber1;
    private Integer fieldNumber2;
    private int fieldNumber3;

    private int fieldWithDefaultValue = 7;

    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
    }

    public String getFieldNumber1() {
      return fieldNumber1;
    }

    public void setFieldNumber1(String fieldNumber1) {
      this.fieldNumber1 = fieldNumber1;
    }

    public Integer getFieldNumber2() {
      return fieldNumber2;
    }

    public void setFieldNumber2(Integer fieldNumber2) {
      this.fieldNumber2 = fieldNumber2;
    }

    public int getFieldNumber3() {
      return fieldNumber3;
    }

    public void setFieldNumber3(int fieldNumber3) {
      this.fieldNumber3 = fieldNumber3;
    }

    public int getFieldWithDefaultValue() {
      return fieldWithDefaultValue;
    }

    public void setFieldWithDefaultValue(int fieldWithDefaultValue) {
      this.fieldWithDefaultValue = fieldWithDefaultValue;
    }
  }
}
