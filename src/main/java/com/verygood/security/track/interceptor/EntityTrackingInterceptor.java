package com.verygood.security.track.interceptor;

import com.verygood.security.track.listener.EntityTrackingListener;

import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import java.io.Serializable;

import javax.annotation.concurrent.ThreadSafe;

// todo: not implemented yet
@ThreadSafe
public class EntityTrackingInterceptor extends EmptyInterceptor implements EntityTrackingListenerAware {

  private EntityTrackingListener entityTrackingListener;

  @Override
  public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
    entityTrackingListener.onEntityChanged(null);
    return false;
  }

  @Override
  public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
    entityTrackingListener.onEntityChanged(null);
  }

  @Override
  public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) throws CallbackException {
    super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
    entityTrackingListener.onEntityChanged(null);
    return false;
  }

  @Override
  public void setEntityTrackingListener(EntityTrackingListener entityTrackingListener) {
    this.entityTrackingListener = entityTrackingListener;
  }
}
