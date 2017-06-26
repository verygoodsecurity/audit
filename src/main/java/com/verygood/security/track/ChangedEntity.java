package com.verygood.security.track;

import com.google.common.base.Objects;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class ChangedEntity {
  private final Serializable id;
  private final Class clazz;
  private final Action action;
  private final List<ChangedEntityField> changedFields;

  ChangedEntity(Serializable id, Class clazz, Action action, List<ChangedEntityField> changedFields) {
    this.id = id;
    this.clazz = clazz;
    this.action = action;
    this.changedFields = changedFields;
  }

  public Serializable getId() {
    return id;
  }

  Class getClazz() {
    return clazz;
  }

  List<ChangedEntityField> getModifiedFields() {
    return Collections.unmodifiableList(changedFields);
  }

  @Override
  public String toString() {
    return "ChangedEntity{" +
        "id=" + id +
        ", clazz=" + clazz +
        ", action=" + action +
        ", changedFields=" + changedFields +
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
