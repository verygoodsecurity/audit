package io.vgs.track.interceptor.transaction;

import io.vgs.track.exception.IllegalTrackingAnnotationsException;
import io.vgs.track.meta.NotTracked;
import io.vgs.track.meta.Trackable;
import io.vgs.track.meta.Tracked;

import org.junit.Test;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

public class BothTrackedAndNotTrackedTest extends BaseTransactionTest {

  @Test(expected = IllegalTrackingAnnotationsException.class)
  public void shouldThrowAnExceptionWhenFieldHasTrackedAndNotTrackedAnnotations() {
    doInJpa(em -> {
      Fruit fruit = new Fruit();
      fruit.setColor("green");
      em.persist(fruit);
    });
  }

  @Test(expected = IllegalTrackingAnnotationsException.class)
  public void shouldThrowAnExceptionWhenFieldHasTrackedAndNotTrackedAnnotationsAndClassHasTracked() {
    doInJpa(em -> {
      Shop shop = new Shop();
      shop.setName("macys");
      em.persist(shop);
    });
  }

  @Override
  protected Class<?>[] entities() {
    return new Class<?>[]{
        Fruit.class,
        Shop.class
    };
  }

  @Entity
  @Trackable
  private static class Fruit {
    @Id
    @GeneratedValue
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
    @GeneratedValue
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
