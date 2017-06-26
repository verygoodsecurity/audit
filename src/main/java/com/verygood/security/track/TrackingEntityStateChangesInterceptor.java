package com.verygood.security.track;

import com.verygood.security.track.data.Action;
import com.verygood.security.track.data.TrackableEntity;
import com.verygood.security.track.exception.IllegalTrackingInterceptorStateException;

import org.hibernate.EmptyInterceptor;

import java.io.Serializable;
import java.util.Optional;

import static com.verygood.security.track.TrackableEntityFactory.createTrackableEntity;

public class TrackingEntityStateChangesInterceptor extends EmptyInterceptor {
  // can't use constructor injection cause interceptor is created using new operator
  private transient EntityStateTrackReporter entityStateTrackReporter;

  public void setEntityStateTrackReporter(EntityStateTrackReporter entityStateTrackReporter) {
    this.entityStateTrackReporter = entityStateTrackReporter;
    checkEntityChangesTrackerExistence();
  }

  @Override
  public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, org.hibernate.type.Type[] types) {
    checkEntityChangesTrackerExistence();
    Optional<TrackableEntity> trackableEntity = createTrackableEntity(id, entity, new Object[]{}, state, propertyNames, Action.SAVE);
    trackableEntity.ifPresent(entityStateTrackReporter::onSave);
    return false;
  }

  @Override
  public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, org.hibernate.type.Type[] types) {
    checkEntityChangesTrackerExistence();
    Optional<TrackableEntity> trackableEntity = createTrackableEntity(id, entity, previousState, currentState, propertyNames, Action.UPDATE);
    trackableEntity.ifPresent(entityStateTrackReporter::onUpdate);
    return false;
  }

  @Override
  public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, org.hibernate.type.Type[] types) {
    checkEntityChangesTrackerExistence();
    Optional<TrackableEntity> trackableEntity = createTrackableEntity(id, entity, state, new Object[]{}, propertyNames, Action.DELETE);
    trackableEntity.ifPresent(entityStateTrackReporter::onDelete);
  }

  private void checkEntityChangesTrackerExistence() {
    if (entityStateTrackReporter == null) {
      throw new IllegalTrackingInterceptorStateException();
    }
  }
}
