package com.verygood.security.track.data;

import com.google.common.base.Objects;

public class EntityTrackingFieldData {
  private String name;
  private Object oldValue;
  private Object newValue;

  public EntityTrackingFieldData(String name, Object oldValue, Object newValue) {
    this.name = name;
    this.oldValue = oldValue;
    this.newValue = newValue;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Object getOldValue() {
    return oldValue;
  }

  public void setOldValue(Object oldValue) {
    this.oldValue = oldValue;
  }

  public Object getNewValue() {
    return newValue;
  }

  public void setNewValue(Object newValue) {
    this.newValue = newValue;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof EntityTrackingFieldData)) return false;
    EntityTrackingFieldData that = (EntityTrackingFieldData) o;
    return Objects.equal(this.name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(name);
  }


}
