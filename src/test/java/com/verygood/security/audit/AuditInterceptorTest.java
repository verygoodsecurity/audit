package com.verygood.security.audit;

import com.verygood.security.audit.entity.Account;
import com.verygood.security.audit.entity.Address;
import com.verygood.security.audit.entity.Client;
import com.verygood.security.audit.sqltracker.QueryCountInfoHolder;

import org.junit.After;
import org.junit.Test;

import java.util.List;

import static com.verygood.security.audit.sqltracker.AssertSqlCount.assertDeleteCount;
import static com.verygood.security.audit.sqltracker.AssertSqlCount.assertInsertCount;
import static com.verygood.security.audit.sqltracker.AssertSqlCount.assertSqlCount;
import static com.verygood.security.audit.sqltracker.AssertSqlCount.assertUpdateCount;
import static org.hamcrest.CoreMatchers.is;
import static org.hibernate.testing.transaction.TransactionUtil.doInJPA;
import static org.junit.Assert.assertThat;

public class AuditInterceptorTest extends BaseTest {

  @After
  public void cleanUp() {
    clearContext();
  }

  private void clearContext() {
    QueryCountInfoHolder.clear();
    TestAuditHandler.getInstance().clear();
  }

  @Test
  public void shouldAuditInsert() {

    doInJPA(() -> emf, em -> {
      Client client = new Client();
      client.setName("Igor");

      Account account = new Account();
      account.setAmount(100);

      client.addAccount(account);
      Address address = new Address();
      client.addAddress(address);

      em.persist(client);
      em.flush();

      List<ModifiedEntityAudit> inserts = TestAuditHandler.getInstance().getInserts();
      List<ModifiedEntityAudit> updates = TestAuditHandler.getInstance().getUpdates();
      List<ModifiedEntityAudit> deletes = TestAuditHandler.getInstance().getDeletes();

      assertThat(inserts.size(), is(3));
      assertThat(updates.size(), is(0));
      assertThat(deletes.size(), is(0));

      assertInsertCount(3);
      assertSqlCount(6);
    });
  }

  @Test
  public void shouldAuditUpdate() {
    setUpClient();

    doInJPA(() -> emf, em -> {
      Client client = em.createQuery("select c from Client c", Client.class).setMaxResults(1).getSingleResult();
      client.setName("new name");

      em.flush();

      List<ModifiedEntityAudit> inserts = TestAuditHandler.getInstance().getInserts();
      List<ModifiedEntityAudit> updates = TestAuditHandler.getInstance().getUpdates();
      List<ModifiedEntityAudit> deletes = TestAuditHandler.getInstance().getDeletes();

      assertThat(inserts.size(), is(0));
      assertThat(updates.size(), is(1));
      assertThat(deletes.size(), is(0));

      assertUpdateCount(1);
      assertSqlCount(2);
    });
  }

  @Test
  public void shouldAuditDelete() {
    setUpClient();

    doInJPA(() -> emf, em -> {
      Client client = em.createQuery("select c from Client c", Client.class).setMaxResults(1).getSingleResult();
      em.remove(client);

      em.flush();

      List<ModifiedEntityAudit> inserts = TestAuditHandler.getInstance().getInserts();
      List<ModifiedEntityAudit> updates = TestAuditHandler.getInstance().getUpdates();
      List<ModifiedEntityAudit> deletes = TestAuditHandler.getInstance().getDeletes();

      assertThat(inserts.size(), is(0));
      assertThat(updates.size(), is(0));
      assertThat(deletes.size(), is(1));

      assertDeleteCount(1);
      assertSqlCount(2);
    });
  }

  private void setUpClient() {
    doInJPA(() -> emf, em -> {
      Client client = new Client();
      Account account1 = new Account();
      Account account2 = new Account();
      Account account3 = new Account();
      client.addAccount(account1);
      client.addAccount(account2);
      client.addAccount(account3);

      Address address = new Address();
      client.addAddress(address);
      em.persist(client);
    });
    clearContext();
  }
}
