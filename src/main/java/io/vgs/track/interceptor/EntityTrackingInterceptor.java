package io.vgs.track.interceptor;

import java.io.Serializable;

import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import io.vgs.track.listener.EntityTrackingListener;

public class EntityTrackingInterceptor extends EmptyInterceptor implements EntityTrackingListenerAware {

    private EntityTrackingListener entityTrackingListener;

    @Override
    public void onDelete(final Object entity, final Serializable id, final Object[] state, final String[] propertyNames,
            final Type[] types) throws CallbackException {
        entityTrackingListener.onEntityChanged(null);
    }

    @Override
    public boolean onFlushDirty(final Object entity, final Serializable id, final Object[] currentState,
            final Object[] previousState, final String[] propertyNames, final Type[] types) throws CallbackException {
        super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
        entityTrackingListener.onEntityChanged(null);
        return false;
    }

    @Override
    public boolean onSave(final Object entity, final Serializable id, final Object[] state,
            final String[] propertyNames, final Type[] types) throws CallbackException {
        entityTrackingListener.onEntityChanged(null);
        return false;
    }

    @Override
    public void setEntityTrackingListener(final EntityTrackingListener entityTrackingListener) {
        this.entityTrackingListener = entityTrackingListener;
    }
}
