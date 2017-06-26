package com.verygood.security.track.entity;

import com.verygood.security.track.meta.Trackable;
import com.verygood.security.track.meta.Tracked;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@Trackable
public class Address {
  @Id
  @GeneratedValue
  private Long id;

  @Tracked
  private String street;

  @OneToOne(fetch = FetchType.LAZY)
  @PrimaryKeyJoinColumn
  private Client client;

  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public Client getClient() {
    return client;
  }

  public void setClient(Client client) {
    this.client = client;
  }
}
