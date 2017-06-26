package com.verygood.security.track;

import com.verygood.security.track.entity.Account;
import com.verygood.security.track.entity.Address;
import com.verygood.security.track.entity.Client;
import com.verygood.security.track.interceptor.TestEntityChangesTracker;
import com.verygood.security.track.sqltracker.QueryCountInfoHolder;

import org.junit.After;
import org.junit.Test;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import static com.verygood.security.track.sqltracker.AssertSqlCount.assertDeleteCount;
import static com.verygood.security.track.sqltracker.AssertSqlCount.assertInsertCount;
import static com.verygood.security.track.sqltracker.AssertSqlCount.assertSqlCount;
import static com.verygood.security.track.sqltracker.AssertSqlCount.assertUpdateCount;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hibernate.testing.transaction.TransactionUtil.doInJPA;
import static org.junit.Assert.assertThat;

public class TrackingEntityChangesInterceptorTest extends BaseTest {

  @After
  public void cleanUp() {
    clearContext();
  }

  private void clearContext() {
    QueryCountInfoHolder.clear();
    TestEntityChangesTracker.getInstance().clear();
  }

  @Test
  public void shouldTrackEntityInsertion() {
    doInJPA(this::entityManagerFactory, em -> {
      Address address = new Address();
      em.persist(address);

      Client client = new Client(address);
      client.setName("Igor");

      Account account = new Account();
      account.setAmount(100);
      client.addAccount(account);

      em.persist(client);
      em.flush();

      List<ChangedEntity> inserts = TestEntityChangesTracker.getInstance().getInserts();
      List<ChangedEntity> updates = TestEntityChangesTracker.getInstance().getUpdates();
      List<ChangedEntity> deletes = TestEntityChangesTracker.getInstance().getDeletes();

      assertThat(inserts.size(), is(3));
      assertThat(updates.size(), is(0));
      assertThat(deletes.size(), is(0));

      Map<Class, List<ChangedEntityField>> map = inserts.stream()
          .collect(toMap(ChangedEntity::getClazz, ChangedEntity::getModifiedFields));
      assertThat(map.get(Client.class).size(), is(2));
      assertThat(map.get(Account.class).size(), is(2));
      assertThat(map.get(Address.class).size(), is(0));

      assertInsertCount(3);
      assertSqlCount(3);
    });
  }

  @Test
  public void shouldTrackEntityUpdating() {
    Serializable clientId = setUpClient();

    doInJPA(this::entityManagerFactory, em -> {
      Client client = em.find(Client.class, clientId);
      client.setName("new name");

      em.flush();

      List<ChangedEntity> inserts = TestEntityChangesTracker.getInstance().getInserts();
      List<ChangedEntity> updates = TestEntityChangesTracker.getInstance().getUpdates();
      List<ChangedEntity> deletes = TestEntityChangesTracker.getInstance().getDeletes();

      assertThat(inserts.size(), is(0));
      assertThat(updates.size(), is(1));
      assertThat(deletes.size(), is(0));

      Map<Class, List<ChangedEntityField>> map = updates.stream()
          .collect(toMap(ChangedEntity::getClazz, ChangedEntity::getModifiedFields));
      assertThat(map.get(Client.class).size(), is(1));
      assertThat(map.get(Account.class), is(nullValue()));
      assertThat(map.get(Address.class), is(nullValue()));

      assertUpdateCount(1);
      assertSqlCount(2);
    });
  }

  @Test
  public void shouldTrackEntityDeletion() {
    Serializable clientId = setUpClient();

    doInJPA(this::entityManagerFactory, em -> {

      Client client = em.find(Client.class, clientId);
      Address address = client.getAddress();
      em.remove(address);

      em.flush();

      List<ChangedEntity> inserts = TestEntityChangesTracker.getInstance().getInserts();
      List<ChangedEntity> updates = TestEntityChangesTracker.getInstance().getUpdates();
      List<ChangedEntity> deletes = TestEntityChangesTracker.getInstance().getDeletes();

      assertThat(inserts.size(), is(0));
      assertThat(updates.size(), is(0));
      assertThat(deletes.size(), is(1));

      Map<Class, List<ChangedEntityField>> map = deletes.stream()
          .collect(toMap(ChangedEntity::getClazz, ChangedEntity::getModifiedFields));
      assertThat(map.get(Client.class), is(nullValue()));
      assertThat(map.get(Account.class), is(nullValue()));
      assertThat(map.get(Address.class).size(), is(1));

      assertDeleteCount(1);
      assertSqlCount(3);
    });
  }

  private Serializable setUpClient() {
    Long clientId = doInJPA(this::entityManagerFactory, em -> {
      Address address = new Address();
      em.persist(address);

      Client client = new Client(address);
      Account account1 = new Account();
      Account account2 = new Account();
      Account account3 = new Account();
      client.addAccount(account1);
      client.addAccount(account2);
      client.addAccount(account3);

      em.persist(client);
      return client.getId();
    });
    clearContext();
    return clientId;
  }

  @Test
  public void shouldTrackAllEntityFieldsOnInsert() {
    doInJPA(this::entityManagerFactory, em -> {
      Address address = new Address();
      address.setCity("test city");
      address.setStreet("test street");
      em.persist(address);
      em.flush();

      List<ChangedEntity> inserts = TestEntityChangesTracker.getInstance().getInserts();
      List<ChangedEntity> updates = TestEntityChangesTracker.getInstance().getUpdates();
      List<ChangedEntity> deletes = TestEntityChangesTracker.getInstance().getDeletes();

      assertThat(inserts.size(), is(1));
      assertThat(updates.size(), is(0));
      assertThat(deletes.size(), is(0));

      Map<Class, List<ChangedEntityField>> map = inserts.stream()
          .collect(toMap(ChangedEntity::getClazz, ChangedEntity::getModifiedFields));
      assertThat(map.get(Client.class), is(nullValue()));
      assertThat(map.get(Account.class), is(nullValue()));
      assertThat(map.get(Address.class).size(), is(2));

      assertInsertCount(1);
      assertSqlCount(1);
    });
  }
}
