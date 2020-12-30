package io.vgs.track.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class EntityTrackingData {
  private final Serializable id;
  private final Class clazz;
  private final Action action;
  private final List<EntityTrackingFieldData> entityTrackingFields = new ArrayList<>();

  public EntityTrackingData(Serializable id, Class clazz, Action action) {
    this.id = id;
    this.clazz = clazz;
    this.action = action;
  }

  public void addOrUpdateEntityTrackingField(EntityTrackingFieldData fieldData, Object newValue) {
    // if new field already exists then just refresh its newValue, otherwise add a new one
    int indexOfExistentField = entityTrackingFields.indexOf(fieldData);
    if (indexOfExistentField >= 0) {
      EntityTrackingFieldData currentField = entityTrackingFields.get(indexOfExistentField);
      currentField.setNewValue(newValue);
    } else {
      entityTrackingFields.add(fieldData);
    }
  }

  public void addEntityTrackingField(EntityTrackingFieldData fieldData) {
    this.entityTrackingFields.add(fieldData);
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

  public List<EntityTrackingFieldData> getEntityTrackingFields() {
    return Collections.unmodifiableList(entityTrackingFields);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof EntityTrackingData)) return false;
    EntityTrackingData that = (EntityTrackingData) o;
    return Objects.equals(this.id, that.id)
        && Objects.equals(this.clazz, that.clazz);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, clazz);
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
}
