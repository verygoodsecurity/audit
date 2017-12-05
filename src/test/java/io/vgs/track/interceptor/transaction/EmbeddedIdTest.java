package io.vgs.track.interceptor.transaction;

import org.junit.Test;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import io.vgs.track.BaseTest;
import io.vgs.track.data.EntityTrackingData;
import io.vgs.track.meta.Trackable;
import io.vgs.track.meta.Tracked;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class EmbeddedIdTest extends BaseTest {

  @Test
  public void save() {
    UserId userId = new UserId("John", "NY");

    doInJpa(em -> {
      User user = new User(userId);
      user.setName("John");
      em.persist(user);
    });

    List<EntityTrackingData> inserts = testEntityTrackingListener.getInserts();
    assertThat(inserts.size(), is(1));
    EntityTrackingData insertData = inserts.get(0);
    assertThat(insertData.getId(), is(userId));
  }

  @Entity
  @Tracked
  @Trackable
  private static class User {
    @EmbeddedId
    private UserId id;

    private String name;

    public User(UserId id) {
      this.id = id;
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
    return new Class[]{
        User.class
    };
  }

  @Embeddable
  private static class UserId implements Serializable {
    private String userName;
    private String departmentNr;

    public UserId(String userName, String departmentNr) {
      this.userName = userName;
      this.departmentNr = departmentNr;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      UserId userId = (UserId) o;

      if (userName != null ? !userName.equals(userId.userName) : userId.userName != null)
        return false;
      return departmentNr != null ? departmentNr.equals(userId.departmentNr) : userId.departmentNr == null;
    }

    @Override
    public int hashCode() {
      int result = userName != null ? userName.hashCode() : 0;
      result = 31 * result + (departmentNr != null ? departmentNr.hashCode() : 0);
      return result;
    }
  }
}
