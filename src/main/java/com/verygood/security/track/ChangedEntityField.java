package com.verygood.security.track;

import com.google.common.base.Objects;

public class ChangedEntityField {
  private final String name;
  private final Object oldValue;
  private final Object newValue;

  ChangedEntityField(String name, Object oldValue, Object newValue) {
    this.name = name;
    this.oldValue = oldValue;
    this.newValue = newValue;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ChangedEntityField)) return false;
    ChangedEntityField that = (ChangedEntityField) o;
    return Objects.equal(this.name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(name);
  }

  @Override
  public String toString() {
    return "ChangedEntityField{" +
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
