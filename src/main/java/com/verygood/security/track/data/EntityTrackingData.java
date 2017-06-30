package com.verygood.security.track.data;

import com.google.common.base.Objects;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

public class EntityTrackingData {
  private final Serializable id;
  private final Class clazz;
  private final Action action;
  private final Set<EntityTrackingFieldData> entityTrackingFields;

  public EntityTrackingData(Serializable id, Class clazz, Action action, Set<EntityTrackingFieldData> entityTrackingFields) {
    this.id = id;
    this.clazz = clazz;
    this.action = action;
    this.entityTrackingFields = entityTrackingFields;
  }

  public Set<EntityTrackingFieldData> getEntityTrackingFields() {
    return Collections.unmodifiableSet(entityTrackingFields);
  }

  public Serializable getId() {
    return id;
  }

  public Class getClazz() {
    return clazz;
  }

  public Action getAction() {
    return action;
  }

  @Override
  public String toString() {
    return "EntityTrackingData{" +
        "id=" + id +
        ", clazz=" + clazz +
        ", action=" + action +
        ", entityTrackingFields=" + entityTrackingFields +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof EntityTrackingData)) return false;
    EntityTrackingData that = (EntityTrackingData) o;
    return Objects.equal(this.id, that.id)
        && Objects.equal(this.clazz, that.clazz)
        && action == that.action;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id, clazz, action);
  }
}
