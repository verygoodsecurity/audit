package com.verygood.security.track.data;

import com.google.common.base.Objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EntityTrackingData {
  private Serializable id;
  private Class clazz;
  private Action action;
  private List<EntityTrackingFieldData> entityTrackingFields = new ArrayList<>();

  public List<EntityTrackingFieldData> getEntityTrackingFields() {
    return entityTrackingFields;
  }

  public Serializable getId() {
    return id;
  }

  public void setId(Serializable id) {
    this.id = id;
  }

  public Class getClazz() {
    return clazz;
  }

  public void setClazz(Class clazz) {
    this.clazz = clazz;
  }

  public Action getAction() {
    return action;
  }

  public void setAction(Action action) {
    this.action = action;
  }

  public void setEntityTrackingFields(List<EntityTrackingFieldData> entityTrackingFields) {
    this.entityTrackingFields = entityTrackingFields;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof EntityTrackingData)) return false;
    EntityTrackingData that = (EntityTrackingData) o;
    return Objects.equal(this.id, that.id)
        && Objects.equal(this.clazz, that.clazz);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id, clazz);
  }
}
