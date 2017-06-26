package com.verygood.security.track;

import com.google.common.base.Objects;

import java.io.Serializable;
import java.util.List;

public class ChangedEntity {
  private final Serializable id;
  private final Class clazz;
  private final Action action;
  private final List<ChangedEntityField> modifiedFields;

  ChangedEntity(Serializable id, Class clazz, Action action, List<ChangedEntityField> modifiedFields) {
    this.id = id;
    this.clazz = clazz;
    this.action = action;
    this.modifiedFields = modifiedFields;
  }

  public Serializable getId() {
    return id;
  }

  public Class getClazz() {
    return clazz;
  }

  public List<ChangedEntityField> getModifiedFields() {
    return modifiedFields;
  }

  @Override
  public String toString() {
    return "ChangedEntity{" +
        "id=" + id +
        ", clazz='" + clazz + '\'' +
        ", action=" + action +
        ", modifiedFields=" + modifiedFields +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ChangedEntity)) return false;
    ChangedEntity that = (ChangedEntity) o;
    return Objects.equal(this.id, that.id)
        && Objects.equal(this.clazz, that.clazz)
        && action == that.action;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id, clazz, action);
  }
}
