package io.vgs.track.interceptor;

import io.vgs.track.data.Action;
import io.vgs.track.data.EntityTrackingData;
import io.vgs.track.data.EntityTrackingFieldData;
import io.vgs.track.exception.IllegalTrackingAnnotationsException;
import io.vgs.track.listener.EntityTrackingListener;
import io.vgs.track.meta.NotTracked;
import io.vgs.track.meta.Trackable;
import io.vgs.track.meta.Tracked;
import io.vgs.track.utils.Utils;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.hibernate.EmptyInterceptor;
import org.hibernate.Transaction;
import org.hibernate.proxy.HibernateProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.concurrent.NotThreadSafe;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.PrimaryKeyJoinColumn;

@NotThreadSafe
public class EntityTrackingTransactionInterceptor extends EmptyInterceptor implements EntityTrackingListenerAware {
  private EntityTrackingListener entityTrackingListener;
  private Map<EntityTrackingData, EntityTrackingData> changes = new HashMap<>();

  private static final Logger LOG = LoggerFactory.getLogger(EntityTrackingTransactionInterceptor.class);

  @Override
  public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, org.hibernate.type.Type[] types) {
    super.onSave(entity, id, state, propertyNames, types);
    createTrackableEntity(id, entity, new Object[]{}, state, propertyNames, Action.CREATED);
    return false;
  }

  @Override
  public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, org.hibernate.type.Type[] types) {
    super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
    createTrackableEntity(id, entity, previousState, currentState, propertyNames, Action.UPDATED);
    return false;
  }

  @Override
  public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, org.hibernate.type.Type[] types) {
    super.onDelete(entity, id, state, propertyNames, types);
    createTrackableEntity(id, entity, state, new Object[]{}, propertyNames, Action.DELETED);
  }

  private void createTrackableEntity(Serializable id, Object entity, Object[] previousState, Object[] currentState, String[] propertyNames, Action action) {
    if (!entity.getClass().isAnnotationPresent(Trackable.class)) {
      return;
    }
    EntityTrackingData entityData = new EntityTrackingData();
    entityData.setId(id);
    entityData.setClazz(entity.getClass());
    entityData.setAction(action);

    //  -----
    boolean areTrackedAllFields = entity.getClass().isAnnotationPresent(Tracked.class);
    for (Field field : entity.getClass().getDeclaredFields()) {
      boolean isTrackedField = field.isAnnotationPresent(Tracked.class);
      boolean isNotTrackedField = field.isAnnotationPresent(NotTracked.class);

      if (isTrackedField && isNotTrackedField) {
        throw new IllegalTrackingAnnotationsException("The field " + field.getName() + " should have either @Tracked or @NotTracked annotation");
      }

      if ((isTrackedField && !isNotTrackedField) || (!isTrackedField && areTrackedAllFields && !isNotTrackedField)) {
        for (int i = 0; i < propertyNames.length; i++) {
          if (propertyNames[i].equals(field.getName())) {
            Object oldValue = previousState.length > 0 ? previousState[i] : null;
            Object newValue = currentState.length > 0 ? currentState[i] : null;

            if (oldValue instanceof Collection || newValue instanceof Collection) {
              // TODO implement @ElementCollection
              continue;
            }

            if (isEntity(oldValue)) {
              if (!tableContainsColumn(field)) {
                continue;
              }
              oldValue = retrieveId(oldValue);
            }

            if (isEntity(newValue)) {
              if (!tableContainsColumn(field)) {
                continue;
              }
              newValue = retrieveId(newValue);
            }

            boolean equal = Utils.equalsOrCompareEquals(oldValue, newValue);

            if (equal) {
              continue;
            }

            EntityTrackingFieldData fieldData = new EntityTrackingFieldData(field.getName(), oldValue, newValue);
            EntityTrackingData entityTrackingData = changes.get(entityData);
            if (entityTrackingData != null) {
              List<EntityTrackingFieldData> trackingFields = entityTrackingData.getEntityTrackingFields();
              // if new field already exists then just refresh its value, otherwise add a new field
              int indexOfExistentField = trackingFields.indexOf(fieldData);
              if (indexOfExistentField >= 0) {
                EntityTrackingFieldData currentField = trackingFields.get(indexOfExistentField);
                currentField.setNewValue(newValue);
              } else {
                trackingFields.add(fieldData);
              }
            } else {
              entityData.getEntityTrackingFields().add(fieldData);
              changes.put(entityData, entityData);
            }
          }
        }
      }
    }
  }

  private boolean tableContainsColumn(Field field) {
    return field.isAnnotationPresent(JoinColumn.class)
        || field.isAnnotationPresent(PrimaryKeyJoinColumn.class);
  }

  private boolean isEntity(Object value) {
    return value != null && value.getClass().isAnnotationPresent(Entity.class);
  }

  private Object retrieveId(Object entity) {
    if (entity == null) {
      throw new IllegalArgumentException("Passed entity cannot be null");
    }
    if (entity instanceof HibernateProxy) {
      return ((HibernateProxy) entity).getHibernateLazyInitializer().getIdentifier();
    }
    return retrieveWithReflection(entity);
  }

  private Object retrieveWithReflection(Object entity) {
    for (Field field : FieldUtils.getAllFieldsList(entity.getClass())) {
      if (field.isAnnotationPresent(Id.class)) {
        try {
          field.setAccessible(true);
          return field.get(entity);
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
    }
    return null;
  }

  @Override
  public void beforeTransactionCompletion(Transaction tx) {
    if (!tx.isActive()) {
      return;
    }
    try {
      if (!changes.isEmpty()) {
        if (entityTrackingListener != null) {
          changes.values().forEach(entityTrackingListener::onEntityChanged);
        } else {
          LOG.warn("Couldn't track data, EntityTrackingListener is not provided");
        }
      }
    } finally {
      changes.clear();
    }
  }

  @Override
  public void setEntityTrackingListener(EntityTrackingListener entityTrackingListener) {
    this.entityTrackingListener = entityTrackingListener;
  }
}
