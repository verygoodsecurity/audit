package com.verygood.security.track;

import com.verygood.security.track.data.Action;
import com.verygood.security.track.meta.Trackable;

import org.hibernate.EmptyInterceptor;

import java.io.Serializable;

public class TrackingEntityStateChangesInterceptor extends EmptyInterceptor {
  // can't use constructor injection cause interceptor is created using new operator
  private TestEntityStateTrackReporter testEntityStateTrackReporter;

  public void setTestEntityStateTrackReporter(TestEntityStateTrackReporter testEntityStateTrackReporter) {
    this.testEntityStateTrackReporter = testEntityStateTrackReporter;
    checkAuditHandler();
  }

  @Override
  public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, org.hibernate.type.Type[] types) {
    checkAuditHandler();
    if (entity.getClass().isAnnotationPresent(Trackable.class)) {
      testEntityStateTrackReporter.onSave(
          TrackableEntityFactory.createModifiedEntityAudit(
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
    checkAuditHandler();
    if (entity.getClass().isAnnotationPresent(Trackable.class)) {
      testEntityStateTrackReporter.onUpdate(
          TrackableEntityFactory.createModifiedEntityAudit(
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
    checkAuditHandler();
    if (entity.getClass().isAnnotationPresent(Trackable.class)) {
      testEntityStateTrackReporter.onDelete(
          TrackableEntityFactory.createModifiedEntityAudit(
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

  private void checkAuditHandler() {
    if (testEntityStateTrackReporter == null) {
      throw new IllegalStateException("Please provide AuditHandler");
    }
  }
}
