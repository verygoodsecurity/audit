package com.verygood.security.track.interceptor;

import com.verygood.security.track.data.Action;
import com.verygood.security.track.data.EntityTrackingData;
import com.verygood.security.track.data.EntityTrackingFieldData;
import com.verygood.security.track.exception.IllegalTrackingAnnotationsException;
import com.verygood.security.track.meta.NotTracked;
import com.verygood.security.track.meta.Trackable;
import com.verygood.security.track.meta.Tracked;
import com.verygood.security.track.utils.Utils;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.hibernate.Transaction;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.resource.transaction.spi.TransactionStatus;

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
public class EntityTrackingTransactionInterceptor extends BaseTrackingInterceptor {
  private Map<EntityTrackingData, EntityTrackingData> changes = new HashMap<>();

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
    if (tx.getStatus() != TransactionStatus.ACTIVE) {
      return;
    }
    try {
      if (!changes.isEmpty()) {
        changes.values().forEach(entityTrackingListener::onEntityChanged);
      }
    } finally {
      changes.clear();
    }
  }
}
