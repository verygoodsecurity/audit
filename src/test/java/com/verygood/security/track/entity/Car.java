package com.verygood.security.track.entity;

import com.verygood.security.track.meta.NotTracked;
import com.verygood.security.track.meta.Trackable;
import com.verygood.security.track.meta.Tracked;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity
@Trackable
public class Car {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_seq")
  @SequenceGenerator(name = "account_seq", sequenceName = "account_seq")
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
