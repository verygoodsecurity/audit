package com.verygood.security.audit.data;

import com.google.common.base.Objects;

import java.io.Serializable;
import java.util.List;

public class TrackableEntity {
  private final Serializable id;
  private final Class clazz;
  private final Action action;
  private final List<TrackableEntityField> modifiedFields;

  public TrackableEntity(Serializable id, Class clazz, Action action, List<TrackableEntityField> modifiedFields) {
    this.id = id;
    this.clazz = clazz;
    this.action = action;
    this.modifiedFields = modifiedFields;
  }

  public Serializable getId() {
    return id;
  }

  public Class getClassName() {
    return clazz;
  }

  public List<TrackableEntityField> getModifiedFields() {
    return modifiedFields;
  }

  @Override
  public String toString() {
    return "ModifiedEntityAudit{" +
        "id=" + id +
        ", clazz='" + clazz + '\'' +
        ", action=" + action +
        ", modifiedFields=" + modifiedFields +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof TrackableEntity)) return false;
    TrackableEntity that = (TrackableEntity) o;
    return Objects.equal(this.id, that.id)
        && Objects.equal(this.clazz, that.clazz)
        && action == that.action;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id, clazz, action);
  }
}
