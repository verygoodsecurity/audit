package io.vgs.track.interceptor;

import io.vgs.track.listener.EntityTrackingListener;

import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import java.io.Serializable;

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
