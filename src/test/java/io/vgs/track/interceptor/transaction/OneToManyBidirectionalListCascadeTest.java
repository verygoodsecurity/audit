package io.vgs.track.interceptor.transaction;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;

import io.vgs.track.BaseTest;
import io.vgs.track.data.EntityTrackingData;
import io.vgs.track.data.EntityTrackingFieldData;
import io.vgs.track.meta.Trackable;
import io.vgs.track.meta.Tracked;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.junit.jupiter.api.Test;

@SuppressWarnings("Duplicates")
public class OneToManyBidirectionalListCascadeTest extends BaseTest {

  @Test
  public void insertWithCascade() {
    doInJpa(em -> {
      Address address = new Address();
      Car car = new Car();

      address.addCar(car);

      em.persist(address);
    });

    List<EntityTrackingData> inserts = testEntityTrackingListener.getInserts();
    assertThat(inserts, hasSize(2));

    EntityTrackingFieldData cars = testEntityTrackingListener.getInsertedField("cars");
    assertThat(cars.getOldValue(), is(nullValue()));
    assertThat(((Collection<?>) cars.getNewValue()), containsInAnyOrder(1L));

    EntityTrackingFieldData address = testEntityTrackingListener.getInsertedField("address");
    assertThat(address.getOldValue(), is(nullValue()));
    assertThat(address.getNewValue(), is(1L));
  }

  @Entity
  @Trackable
  @Tracked
  public static class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "car_seq")
    @SequenceGenerator(name = "car_seq", sequenceName = "car_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_address")
    private Address address;

    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
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
  public static class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "address_seq")
    @SequenceGenerator(name = "address_seq", sequenceName = "address_seq")
    private Long id;

    @OneToMany(mappedBy = "address")
    @Cascade(CascadeType.PERSIST)
    private List<Car> cars = new ArrayList<>();

    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
    }

    public List<Car> getCars() {
      return cars;
    }

    public void setCars(List<Car> cars) {
      this.cars = cars;
    }

    public void addCar(Car car) {
      cars.add(car);
      car.setAddress(this);
    }
  }

  @Override
  protected Class<?>[] entities() {
    return new Class[]{
        Car.class,
        Address.class
    };
  }
}
