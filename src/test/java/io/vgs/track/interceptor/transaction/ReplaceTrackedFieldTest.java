package io.vgs.track.interceptor.transaction;

import static org.hamcrest.MatcherAssert.assertThat;

import io.vgs.track.BaseTest;
import io.vgs.track.data.EntityTrackingData;
import io.vgs.track.data.EntityTrackingFieldData;
import io.vgs.track.meta.Trackable;
import io.vgs.track.meta.Tracked;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

public class ReplaceTrackedFieldTest extends BaseTest {

  @Test
  public void testReplaceValues() {
    final String trackedFieldValue = "tracked field";
    final String anotherTrackedFieldValue = "another tracked field";

    doInJpa(em -> {
      Account account = new Account();
      account.setName("Jonathan");
      account.setVersion(42);
      account.setTracked(trackedFieldValue);
      account.setAnotherTracked(anotherTrackedFieldValue);
      em.persist(account);
    });

    List<EntityTrackingData> trackingData = testEntityTrackingListener.getInserts();

    assertThat(trackingData, Matchers.hasSize(1));

    EntityTrackingFieldData fieldData1 = testEntityTrackingListener.getInsertedField("name");
    assertThat(fieldData1.getNewValue(), CoreMatchers.equalTo("not for your eyes"));
    assertThat(fieldData1.getOldValue(), CoreMatchers.equalTo("not for your eyes"));
    EntityTrackingFieldData fieldData2 = testEntityTrackingListener.getInsertedField("version");
    assertThat(fieldData2.getNewValue(), CoreMatchers.equalTo(Tracked.DEFAULT_REPLACE));
    assertThat(fieldData2.getOldValue(), CoreMatchers.equalTo(Tracked.DEFAULT_REPLACE));
    EntityTrackingFieldData fieldData3 = testEntityTrackingListener.getInsertedField("tracked");
    assertThat(fieldData3.getNewValue(), CoreMatchers.equalTo(trackedFieldValue));
    EntityTrackingFieldData fieldData4 = testEntityTrackingListener.getInsertedField("anotherTracked");
    assertThat(fieldData4.getNewValue(), CoreMatchers.equalTo(anotherTrackedFieldValue));
  }

  @Override
  protected Class<?>[] entities() {
    return new Class<?>[]{
        Account.class
    };
  }

  @Entity
  @Trackable
  public static class Account {

    @Id
    @GeneratedValue
    private Long id;

    @Tracked(replace = true, replaceWith = "not for your eyes")
    private String name;

    @Tracked(replace = true)
    private Integer version;

    @Tracked
    private String tracked;
    @Tracked(replace = false)
    private String anotherTracked;

    public String getTracked() {
      return tracked;
    }

    public void setTracked(String tracked) {
      this.tracked = tracked;
    }

    public String getAnotherTracked() {
      return anotherTracked;
    }

    public void setAnotherTracked(String anotherTracked) {
      this.anotherTracked = anotherTracked;
    }

    public Integer getVersion() {
      return version;
    }

    public void setVersion(Integer version) {
      this.version = version;
    }

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
}
