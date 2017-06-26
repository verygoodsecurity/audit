package com.verygood.security.track.data;

import com.google.common.base.Objects;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

public class TrackableEntity {
  private final Serializable id;
  private final Class clazz;
  private final Action action;
  private final Set<TrackableEntityField> trackableEntityFields;

  public TrackableEntity(Serializable id, Class clazz, Action action, Set<TrackableEntityField> trackableEntityFields) {
    this.id = id;
    this.clazz = clazz;
    this.action = action;
    this.trackableEntityFields = trackableEntityFields;
  }

  public Serializable getId() {
    return id;
  }

  public Class getClazz() {
    return clazz;
  }

  public Set<TrackableEntityField> getTrackableEntityFields() {
    return Collections.unmodifiableSet(trackableEntityFields);
  }

  @Override
  public String toString() {
    return "TrackableEntity{" +
        "id=" + id +
        ", clazz=" + clazz +
        ", action=" + action +
        ", trackableEntityFields=" + trackableEntityFields +
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
