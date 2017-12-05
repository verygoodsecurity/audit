package io.vgs.track.interceptor.transaction;

import com.google.common.collect.ImmutableMap;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import io.vgs.track.BaseTest;
import io.vgs.track.data.EntityTrackingFieldData;
import io.vgs.track.meta.Trackable;
import io.vgs.track.meta.Tracked;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class MapPrimitiveMappingTest extends BaseTest {

  @Test
  public void shouldTrackMapWithPrimitiveValues() {
    Map<String, Integer> store = ImmutableMap.of("store", 1);

    doInJpa(em -> {
      Employee employee = new Employee();
      employee.setConfig(store);
      em.persist(employee);
    });

    EntityTrackingFieldData config = testEntityTrackingListener.getInsertedField("config");
    assertThat(config, is(notNullValue()));
    assertThat(config.getOldValue(), is(nullValue()));
    assertThat(config.getNewValue(), is(store));
  }

  @Entity
  @Trackable
  @Tracked
  private static class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employee_seq")
    @SequenceGenerator(name = "employee_seq", sequenceName = "employee_seq")
    private Long id;

    @ElementCollection
    private Map<String, Integer> config = new HashMap<>();

    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
    }

    public Map<String, Integer> getConfig() {
      return config;
    }

    public void setConfig(Map<String, Integer> config) {
      this.config = config;
    }
  }

  @Override
  protected Class<?>[] entities() {
    return new Class<?>[]{
        Employee.class
    };
  }
}
