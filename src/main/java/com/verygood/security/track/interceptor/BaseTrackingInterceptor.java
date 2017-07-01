package com.verygood.security.track.interceptor;

import com.verygood.security.track.exception.IllegalEntityTrackingInterceptorException;
import com.verygood.security.track.listener.EntityTrackingListener;

import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import java.io.Serializable;

public abstract class BaseTrackingInterceptor extends EmptyInterceptor {
  transient EntityTrackingListener entityTrackingListener;

  public void setEntityTrackingListener(EntityTrackingListener entityTrackingListener) {
    this.entityTrackingListener = entityTrackingListener;
    checkEntityChangesTrackerExistence();
  }

  private void checkEntityChangesTrackerExistence() {
    if (entityTrackingListener == null) {
      throw new IllegalEntityTrackingInterceptorException();
    }
  }

  public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
    checkEntityChangesTrackerExistence();
    return false;
  }

  public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
    checkEntityChangesTrackerExistence();
  }

  public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) throws CallbackException {
    checkEntityChangesTrackerExistence();
    return false;
  }
}
