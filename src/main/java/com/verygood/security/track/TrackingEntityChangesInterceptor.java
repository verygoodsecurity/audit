package com.verygood.security.track;

import org.hibernate.EmptyInterceptor;

import java.io.Serializable;

public class TrackingEntityChangesInterceptor extends EmptyInterceptor {
  // can't use constructor injection cause interceptor is created using new operator
  private transient EntityChangesTracker entityChangesTracker;

  void setEntityChangesTracker(EntityChangesTracker entityChangesTracker) {
    this.entityChangesTracker = entityChangesTracker;
    checkEntityChangesTrackerExistence();
  }

  @Override
  public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, org.hibernate.type.Type[] types) {
    checkEntityChangesTrackerExistence();
    if (entity.getClass().isAnnotationPresent(TrackChanges.class)) {
      entityChangesTracker.onSave(
          EntityChangesFactory.createChangedEntity(
              id,
              entity,
              new Object[]{},
              state,
              propertyNames,
              Action.SAVE
          )
      );
    }
    return false;
  }

  @Override
  public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, org.hibernate.type.Type[] types) {
    checkEntityChangesTrackerExistence();
    if (entity.getClass().isAnnotationPresent(TrackChanges.class)) {
      entityChangesTracker.onUpdate(
          EntityChangesFactory.createChangedEntity(
              id,
              entity,
              previousState,
              currentState,
              propertyNames,
              Action.UPDATE
          )
      );
    }
    return false;
  }

  @Override
  public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, org.hibernate.type.Type[] types) {
    checkEntityChangesTrackerExistence();
    if (entity.getClass().isAnnotationPresent(TrackChanges.class)) {
      entityChangesTracker.onDelete(
          EntityChangesFactory.createChangedEntity(
              id,
              entity,
              state,
              new Object[]{},
              propertyNames,
              Action.DELETE
          )
      );
    }
  }

  private void checkEntityChangesTrackerExistence() {
    if (entityChangesTracker == null) {
      throw new IllegalStateException("Please provide EntityChangesTracker");
    }
  }
}
