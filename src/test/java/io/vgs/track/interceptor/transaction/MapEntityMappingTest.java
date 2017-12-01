package io.vgs.track.interceptor.transaction;

import com.google.common.collect.ImmutableMap;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

import io.vgs.track.BaseTest;
import io.vgs.track.data.EntityTrackingFieldData;
import io.vgs.track.meta.Trackable;
import io.vgs.track.meta.Tracked;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class MapEntityMappingTest extends BaseTest {

  @Test
  public void mapNotSupported() {
    Map<String, Car> cars = new HashMap<>();

    doInJpa(em -> {
      Car greenCar = new Car();
      greenCar.setColor("green");

      Car blueCar = new Car();
      blueCar.setColor("blue");

      Address address = new Address();
      cars.put(greenCar.getColor(), greenCar);
      cars.put(blueCar.getColor(), blueCar);
      address.setCarMap(cars);

      em.persist(greenCar);
      em.persist(blueCar);
      em.persist(address);
    });

    EntityTrackingFieldData carMapField = testEntityTrackingListener.getInsertedField("carMap");
    assertThat(carMapField, is(notNullValue()));
    assertThat(carMapField.getNewValue(), is(cars));
  }

  @Entity
  @Trackable
  @Tracked
  private static class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "car_seq")
    @SequenceGenerator(name = "car_seq", sequenceName = "car_seq")
    private Long id;

    private String color;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_address")
    private Address address;

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

    public Address getAddress() {
      return address;
    }

    public void setAddress(Address address) {
      this.address = address;
    }
  }

  @Entity
  @Trackable
  @Tracked
  private static class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "address_seq")
    @SequenceGenerator(name = "address_seq", sequenceName = "address_seq")
    private Long id;

    @MapKey(name = "color")
    @OneToMany(mappedBy = "address")
    private Map<String, Car> carMap = new HashMap<>();

    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
    }

    public Map<String, Car> getCarMap() {
      return carMap;
    }

    public void setCarMap(Map<String, Car> carMap) {
      this.carMap = carMap;
    }
  }

  @Override
  protected Class<?>[] entities() {
    return new Class<?>[]{
        Car.class,
        Address.class
    };
  }
}
