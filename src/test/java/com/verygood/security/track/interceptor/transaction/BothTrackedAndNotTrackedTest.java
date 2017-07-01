package com.verygood.security.track.interceptor.transaction;

import com.verygood.security.track.exception.IllegalTrackingAnnotationsException;
import com.verygood.security.track.meta.NotTracked;
import com.verygood.security.track.meta.Trackable;
import com.verygood.security.track.meta.Tracked;

import org.junit.Test;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import static org.hibernate.testing.transaction.TransactionUtil.doInJPA;

public class BothTrackedAndNotTrackedTest extends BaseTransactionTest {

  @Test(expected = IllegalTrackingAnnotationsException.class)
  public void shouldThrowAnExceptionWhenFieldHasTrackedAndNotTrackedAnnotations() {
    doInJPA(this::entityManagerFactory, em -> {
      Fruit fruit = new Fruit();
      fruit.setColor("green");
      em.persist(fruit);
    });
  }

  @Test(expected = IllegalTrackingAnnotationsException.class)
  public void shouldThrowAnExceptionWhenFieldHasTrackedAndNotTrackedAnnotationsAndClassHasTracked() {
    doInJPA(this::entityManagerFactory, em -> {
      Fruit fruit = new Fruit();
      fruit.setColor("green");
      em.persist(fruit);
    });
  }

  @Override
  protected Class<?>[] entities() {
    return new Class<?>[]{
        Fruit.class
    };
  }

  @Entity
  @Trackable
  private static class Fruit {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fruit_seq")
    @SequenceGenerator(name = "fruit_seq", sequenceName = "fruit_seq")
    private Long id;

    @Tracked
    @NotTracked
    private String color;

    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
    }

    public String getColor() {
      return color;
    }

    public void setColor(String color) {
      this.color = color;
    }
  }

  @Entity
  @Trackable
  @Tracked
  private static class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "shop_seq")
    @SequenceGenerator(name = "shop_seq", sequenceName = "shop_seq")
    private Long id;

    @Tracked
    @NotTracked
    private String name;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }
  }
}
