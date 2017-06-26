package com.verygood.security.track.entity;

import com.verygood.security.track.TrackChanges;
import com.verygood.security.track.Tracked;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SequenceGenerator;

@Entity
@TrackChanges
@Tracked
public class Address {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "address_seq")
  @SequenceGenerator(name = "address_seq", sequenceName = "address_seq")
  private Long id;

  private String city;
  private String street;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_client")
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

  public Long getId() {
    return id;
  }

  public void setCity(String city) {
    this.city = city;
  }
}
