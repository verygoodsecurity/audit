package com.verygood.security.track.entity;

import com.verygood.security.track.TrackChanges;
import com.verygood.security.track.Tracked;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

@Entity
@TrackChanges
public class Account {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_seq")
  @SequenceGenerator(name = "account_seq", sequenceName = "account_seq")
  @Tracked
  private Long id;

  @Tracked
  private Integer amount;

  @JoinColumn(name = "id_client")
  @ManyToOne(fetch = FetchType.LAZY)
  @Tracked
  private Client client;

  public Integer getAmount() {
    return amount;
  }

  public void setAmount(Integer amount) {
    this.amount = amount;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Client getClient() {
    return client;
  }

  public void setClient(Client client) {
    this.client = client;
  }
}
