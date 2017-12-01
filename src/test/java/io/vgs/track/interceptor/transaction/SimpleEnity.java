package io.vgs.track.interceptor.transaction;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import io.vgs.track.meta.Trackable;
import io.vgs.track.meta.Tracked;

@Entity
@Trackable
@Tracked
public class SimpleEnity {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
  @SequenceGenerator(name = "seq", sequenceName = "seq")
  private Long id;

  private String name;

  @ElementCollection
  private List<String> cars = new ArrayList<>();

  public SimpleEnity() {
  }

  public List<String> getCars() {
    return cars;
  }

  public void setCars(List<String> cars) {
    this.cars = cars;
  }

  public SimpleEnity(String name, List<String> cars) {
    this.name = name;
    this.cars = cars;
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
