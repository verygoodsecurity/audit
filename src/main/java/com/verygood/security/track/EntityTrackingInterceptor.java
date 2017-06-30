package com.verygood.security.track;

import com.verygood.security.track.data.Action;
import com.verygood.security.track.data.EntityTrackingData;
import com.verygood.security.track.exception.IllegalEntityTrackingInterceptorException;

import org.hibernate.EmptyInterceptor;

import java.io.Serializable;
import java.util.Optional;

import static com.verygood.security.track.EntityTrackingFactory.createTrackableEntity;

public class EntityTrackingInterceptor extends EmptyInterceptor {
  // can't use constructor injection cause interceptor is created using new operator
  private transient EntityTrackingListener entityTrackingListener;

  public void setEntityTrackingListener(EntityTrackingListener entityTrackingListener) {
    this.entityTrackingListener = entityTrackingListener;
    checkEntityChangesTrackerExistence();
  }

  @Override
  public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, org.hibernate.type.Type[] types) {
    checkEntityChangesTrackerExistence();
    Optional<EntityTrackingData> trackableEntity = createTrackableEntity(id, entity, new Object[]{}, state, propertyNames, Action.CREATED);
    trackableEntity.ifPresent(entityTrackingListener::onSave);
    return false;
  }

  @Override
  public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, org.hibernate.type.Type[] types) {
    checkEntityChangesTrackerExistence();
    Optional<EntityTrackingData> trackableEntity = createTrackableEntity(id, entity, previousState, currentState, propertyNames, Action.UPDATED);
    trackableEntity.ifPresent(entityTrackingListener::onUpdate);
    return false;
  }

  @Override
  public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, org.hibernate.type.Type[] types) {
    checkEntityChangesTrackerExistence();
    Optional<EntityTrackingData> trackableEntity = createTrackableEntity(id, entity, state, new Object[]{}, propertyNames, Action.DELETED);
    trackableEntity.ifPresent(entityTrackingListener::onDelete);
  }

  private void checkEntityChangesTrackerExistence() {
    if (entityTrackingListener == null) {
      throw new IllegalEntityTrackingInterceptorException();
    }
  }
}
