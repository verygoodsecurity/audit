package com.verygood.security.audit.entity;

import com.verygood.security.audit.meta.Trackable;
import com.verygood.security.audit.meta.Tracked;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
@Trackable
public class Client {
  @Id
  @GeneratedValue
  @Tracked
  private Long id;

  @Tracked
  private String name;

  @OneToMany(mappedBy = "client")
  @Tracked
  @Cascade(CascadeType.PERSIST)
  private List<Account> accounts = new ArrayList<>();

  @OneToOne(mappedBy = "client", fetch = FetchType.LAZY, optional = false)
  @Cascade(CascadeType.PERSIST)
  @Tracked
  private Address address;

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

  public void removeAccount(Account account) {
    this.accounts.remove(account);
    account.setClient(null);
  }

  public void addAddress(Address address) {
    address.setClient(this);
    this.address = address;
  }

  public void removeAddress() {
    if (address != null) {
      address.setClient(null);
      this.address = null;
    }
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
