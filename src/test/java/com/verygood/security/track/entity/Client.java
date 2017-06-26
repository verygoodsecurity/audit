package com.verygood.security.track.entity;

import com.verygood.security.track.TrackChanges;
import com.verygood.security.track.Tracked;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;

@Entity
@TrackChanges
public class Client {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_seq")
  @SequenceGenerator(name = "client_seq", sequenceName = "client_seq")
  @Tracked
  private Long id;

  @Tracked
  private String name;

  @OneToMany(mappedBy = "client")
  @Tracked
  @Cascade(CascadeType.PERSIST)
  private List<Account> accounts = new ArrayList<>();

  @OneToOne(mappedBy = "client", fetch = FetchType.LAZY, optional = false)
  @Tracked
  private Address address;

  public Client() {
  }

  public Client(Address address) {
    this.address = address;
    address.setClient(this);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void addAccount(Account account) {
    this.accounts.add(account);
    account.setClient(this);
  }

  public Address getAddress() {
    return address;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public List<Account> getAccounts() {
    return accounts;
  }

  public void setAccounts(List<Account> accounts) {
    this.accounts = accounts;
  }
}
