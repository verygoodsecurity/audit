package com.verygood.security.track.data;

import com.google.common.base.Objects;

public class TrackableEntityField {
  private final String name;
  private final Object oldValue;
  private final Object newValue;

  public TrackableEntityField(String name, Object oldValue, Object newValue) {
    this.name = name;
    this.oldValue = oldValue;
    this.newValue = newValue;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof TrackableEntityField)) return false;
    TrackableEntityField that = (TrackableEntityField) o;
    return Objects.equal(this.name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(name);
  }

  @Override
  public String toString() {
    return "TrackableEntityField{" +
        "name='" + name + '\'' +
        ", oldValue=" + oldValue +
        ", newValue=" + newValue +
        '}';
  }

  public String getName() {
    return name;
  }

  public Object getOldValue() {
    return oldValue;
  }

  public Object getNewValue() {
    return newValue;
  }


}
