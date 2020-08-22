package io.vgs.track.interceptor.transaction;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;

import io.vgs.track.BaseTest;
import io.vgs.track.data.EntityTrackingData;
import io.vgs.track.data.EntityTrackingFieldData;
import io.vgs.track.meta.Trackable;
import io.vgs.track.meta.Tracked;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;

@SuppressWarnings("Duplicates")
public class ManyToManyUnidirectionalSetTest extends BaseTest {

  @Test
  public void simpleSynchronization() {
    doInJpa(em -> {
      Employee employee = new Employee();
      Project project = new Project();

      employee.getProjects().add(project);

      em.persist(project);
      em.persist(employee);
    });

    List<EntityTrackingData> inserts = testEntityTrackingListener.getInserts();
    assertThat(inserts.size(), is(1));
    EntityTrackingFieldData projects = testEntityTrackingListener.getInsertedField("projects");
    assertThat(projects.getOldValue(), is(nullValue()));
    Collection<?> actualProjectsNewValue = (Collection) projects.getNewValue();
    assertThat(actualProjectsNewValue, hasSize(1));
    assertThat(actualProjectsNewValue, containsInAnyOrder(1L));
  }

  @Test
  public void updateSimpleSynchronization() {
    Long employeeId = doInJpa(em -> {
      Employee employee = new Employee();
      Project project = new Project();
      project.setName("old project");
      employee.getProjects().add(project);
      em.persist(project);
      em.persist(employee);
      return employee.getId();
    });
    clearContext();

    doInJpa(em -> {
      Employee employee = em.find(Employee.class, employeeId);
      Project newProject = new Project();
      newProject.setName("new project");
      employee.getProjects().add(newProject);
      em.persist(newProject);
    });

    List<EntityTrackingData> updates = testEntityTrackingListener.getUpdates();
    assertThat(updates, hasSize(1));

    EntityTrackingFieldData projects = testEntityTrackingListener.getUpdatedField("projects");
    Collection<?> actualOldValue = (Collection) projects.getOldValue();
    Collection<?> actualNewValue = (Collection) projects.getNewValue();

    assertThat(actualOldValue, hasSize(1));
    assertThat(actualOldValue, containsInAnyOrder(1L));

    assertThat(actualNewValue, hasSize(2));
    assertThat(actualNewValue, containsInAnyOrder(1L, 2L));
  }

  @Test
  public void synchronizationOfAlreadyCreatedEntities() {
    Pair<Long, Long> pair = doInJpa(em -> {
      Employee employee = new Employee();
      Project project = new Project();
      em.persist(project);
      em.persist(employee);
      return Pair.of(employee.getId(), project.getId());
    });
    clearContext();

    doInJpa(em -> {
      Employee employee = em.find(Employee.class, pair.getLeft());
      Project project = em.find(Project.class, pair.getRight());

      employee.getProjects().add(project);
    });

    List<EntityTrackingData> updates = testEntityTrackingListener.getUpdates();
    assertThat(updates, hasSize(1));

    EntityTrackingFieldData projects = testEntityTrackingListener.getUpdatedField("projects");
    assertThat(((Collection<?>) projects.getOldValue()), hasSize(0));
    assertThat(((Collection<?>) projects.getNewValue()), hasSize(1));
    assertThat(((Collection<?>) projects.getNewValue()), containsInAnyOrder(1L));
  }

  @Test
  public void shouldTrackRemovedElement() {
    Pair<Long, Long> employeeProjectIds = doInJpa(em -> {
      Employee employee = new Employee();
      Project firstProject = new Project();
      firstProject.setName("first");
      Project secondProject = new Project();
      secondProject.setName("second");

      employee.getProjects().add(firstProject);
      employee.getProjects().add(secondProject);

      em.persist(firstProject);
      em.persist(secondProject);
      em.persist(employee);
      return Pair.of(employee.getId(), firstProject.getId());
    });

    clearContext();

    doInJpa(em -> {
      Employee employee = em.find(Employee.class, employeeProjectIds.getLeft());
      Project project = em.find(Project.class, employeeProjectIds.getRight());
      employee.getProjects().remove(project);
    });

    List<EntityTrackingData> updates = testEntityTrackingListener.getUpdates();
    assertThat(updates, hasSize(1));

    EntityTrackingFieldData projects = testEntityTrackingListener.getUpdatedField("projects");
    assertThat(((Collection<?>) projects.getOldValue()), hasSize(2));
    assertThat(((Collection<?>) projects.getOldValue()), containsInAnyOrder(1L, 2L));
    assertThat(((Collection<?>) projects.getNewValue()), hasSize(1));
    assertThat(((Collection<?>) projects.getNewValue()), containsInAnyOrder(2L));
  }

  @Override
  protected Class<?>[] entities() {
    return new Class<?>[]{
        Employee.class,
        Project.class
    };
  }

  @Entity
  @Tracked
  @Trackable
  public static class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employee_seq")
    @SequenceGenerator(name = "employee_seq", sequenceName = "employee_seq")
    private Long id;

    @ManyToMany
    private Set<Project> projects = new HashSet<>();

    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
    }

    public Set<Project> getProjects() {
      return projects;
    }

    public void setProjects(Set<Project> projects) {
      this.projects = projects;
    }

    @Override
    public String toString() {
      return "Employee{" +
          "id=" + id +
          '}';
    }
  }

  @Entity
  @Tracked
  @Trackable
  public static class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "project_seq")
    @SequenceGenerator(name = "project_seq", sequenceName = "project_seq")
    private Long id;

    private String name;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
    }

    @Override
    public String toString() {
      return "Project{" +
          "id=" + id +
          '}';
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Project project = (Project) o;

      return name != null ? name.equals(project.name) : project.name == null;
    }

    @Override
    public int hashCode() {
      return name != null ? name.hashCode() : 0;
    }
  }
}
