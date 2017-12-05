package io.vgs.track.data;

import com.google.common.base.Objects;

public class EntityTrackingFieldData {
  private final String name;
  private final Object oldValue;
  private Object newValue;

  public EntityTrackingFieldData(String name, Object oldValue, Object newValue) {
    this.name = name;
    this.oldValue = oldValue;
    this.newValue = newValue;
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

  @Override
  public String toString() {
    return "EntityTrackingFieldData{" +
        "name='" + name + '\'' +
        ", oldValue=" + oldValue +
        ", newValue=" + newValue +
        '}';
  }
}
