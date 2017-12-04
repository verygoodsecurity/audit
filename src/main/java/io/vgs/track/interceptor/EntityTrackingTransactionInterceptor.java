package io.vgs.track.interceptor;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.Hibernate;
import org.hibernate.Transaction;
import org.hibernate.collection.internal.AbstractPersistentCollection;
import org.hibernate.collection.internal.PersistentMap;
import org.hibernate.collection.internal.PersistentSet;
import org.hibernate.engine.spi.CollectionEntry;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.concurrent.NotThreadSafe;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import io.vgs.track.data.Action;
import io.vgs.track.data.EntityTrackingData;
import io.vgs.track.data.EntityTrackingFieldData;
import io.vgs.track.exception.IllegalTrackingAnnotationsException;
import io.vgs.track.listener.EntityTrackingListener;
import io.vgs.track.meta.NotTracked;
import io.vgs.track.meta.Trackable;
import io.vgs.track.meta.Tracked;
import io.vgs.track.utils.Utils;

import static java.util.stream.Collectors.toList;

@NotThreadSafe
public class EntityTrackingTransactionInterceptor extends EmptyInterceptor implements EntityTrackingListenerAware {
  private static final Logger LOG = LoggerFactory.getLogger(EntityTrackingTransactionInterceptor.class);
  private static final String TRACK_AND_NOT_TRACK_ANNOTATIONS_ERROR_MSG = "The field %s should have either @Tracked or @NotTracked annotation";
  private static final String PERSISTENT_MAP_TRACKING_IS_NOT_SUPPORTED_YET = "PersistentMap tracking is not supported yet";

  private EntityTrackingListener entityTrackingListener;
  private Map<EntityTrackingData, EntityTrackingData> changes = new HashMap<>();

  @Override
  public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
    super.onSave(entity, id, state, propertyNames, types);
    createEntityTrackingData(id, entity, new Object[]{}, state, propertyNames, Action.CREATED);
    return false;
  }

  @Override
  public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
    super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
    createEntityTrackingData(id, entity, previousState, currentState, propertyNames, Action.UPDATED);
    return false;
  }

  @Override
  public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
    super.onDelete(entity, id, state, propertyNames, types);
    createEntityTrackingData(id, entity, state, new Object[]{}, propertyNames, Action.DELETED);
  }

  @Override
  public void onCollectionUpdate(Object collection, Serializable key) throws CallbackException {
    super.onCollectionUpdate(collection, key);

    // do not touch lazy collections, like PersistentBag
    // for performance sake entity.getPersistentBag().add(element) does not trigger sql select to fetch entire collection
    if (!Hibernate.isInitialized(collection)) {
      LOG.debug("Couldn't track lazy collection");
      return;
    }
    if (!(collection instanceof AbstractPersistentCollection)) {
      return;
    }
    // not implemented yet
    if (collection instanceof PersistentMap) {
      LOG.warn(PERSISTENT_MAP_TRACKING_IS_NOT_SUPPORTED_YET);
      return;
    }

    AbstractPersistentCollection newValues = (AbstractPersistentCollection) collection;
    Object owner = newValues.getOwner();
    if (!owner.getClass().isAnnotationPresent(Trackable.class)) {
      return;
    }

    String collectionFieldName = getFieldNameFromRole(owner, newValues.getRole());
    Field collectionField = FieldUtils.getDeclaredField(owner.getClass(), collectionFieldName, true);
    boolean areTrackedAllFields = owner.getClass().isAnnotationPresent(Tracked.class);
    if (!validateAnnotations(areTrackedAllFields, collectionField)) {
      return;
    }

    Collection<?> oldCollection = (newValues instanceof PersistentSet) ? ((Map<?, ?>) newValues.getStoredSnapshot()).keySet() : (Collection) newValues.getStoredSnapshot();
    Collection<?> newCollection = (Collection) collection;

    boolean isCollectionOfEntities = isCollectionOfEntities(collectionField);
    Object oldValue = isCollectionOfEntities ? oldCollection.stream().map(this::retrieveIdFromEntity).collect(toList()) : oldCollection;
    Object newValue = isCollectionOfEntities ? newCollection.stream().map(this::retrieveIdFromEntity).collect(toList()) : newCollection;

    EntityTrackingData entityData = new EntityTrackingData(key, owner.getClass(), Action.UPDATED);
    EntityTrackingFieldData fieldData = new EntityTrackingFieldData(collectionFieldName, oldValue, newValue);
    addOrUpdateFieldData(entityData, fieldData, newCollection);
  }

  @Override
  public void onCollectionRecreate(Object collection, Serializable key) throws CallbackException {
    super.onCollectionRecreate(collection, key);

    if (!(collection instanceof AbstractPersistentCollection)) {
      return;
    }
    // not implemented yet
    if (collection instanceof PersistentMap) {
      LOG.warn(PERSISTENT_MAP_TRACKING_IS_NOT_SUPPORTED_YET);
      return;
    }

    AbstractPersistentCollection newValues = (AbstractPersistentCollection) collection;

    Object owner = newValues.getOwner();
    if (!owner.getClass().isAnnotationPresent(Trackable.class)) {
      return;
    }

    CollectionEntry collectionEntry = newValues.getSession().getPersistenceContext().getCollectionEntry(newValues);
    String collectionRole = collectionEntry.getCurrentPersister().getRole();
    String collectionFieldName = getFieldNameFromRole(owner, collectionRole);

    Field collectionField = FieldUtils.getDeclaredField(owner.getClass(), collectionFieldName, true);
    boolean areTrackedAllFields = owner.getClass().isAnnotationPresent(Tracked.class);
    if (!validateAnnotations(areTrackedAllFields, collectionField)) {
      return;
    }

    Collection<?> newCollection = (Collection) collection;

    boolean isCollectionOfEntities = isCollectionOfEntities(collectionField);
    Object oldValue = null;
    Object newValue = isCollectionOfEntities ? newCollection.stream().map(this::retrieveIdFromEntity).collect(toList()) : newCollection;

    EntityTrackingData entityData = new EntityTrackingData(key, owner.getClass(), Action.CREATED);
    EntityTrackingFieldData fieldData = new EntityTrackingFieldData(collectionFieldName, oldValue, newValue);
    addOrUpdateFieldData(entityData, fieldData, newCollection);
  }

  private void createEntityTrackingData(Serializable id, Object entity, Object[] previousState, Object[] currentState, String[] propertyNames, Action action) {
    if (!entity.getClass().isAnnotationPresent(Trackable.class)) {
      return;
    }
    EntityTrackingData entityData = new EntityTrackingData(id, entity.getClass(), action);

    boolean areTrackedAllFields = entity.getClass().isAnnotationPresent(Tracked.class);
    for (Field field : entity.getClass().getDeclaredFields()) {
      if (validateAnnotations(areTrackedAllFields, field)) {
        createEntityTrackingFieldData(previousState, currentState, propertyNames, entityData, field);
      }
    }
  }

  private boolean validateAnnotations(boolean areTrackedAllFields, Field field) {
    boolean isTrackedField = field.isAnnotationPresent(Tracked.class);
    boolean isNotTrackedField = field.isAnnotationPresent(NotTracked.class);

    if (isTrackedField && isNotTrackedField) {
      throw new IllegalTrackingAnnotationsException(String.format(TRACK_AND_NOT_TRACK_ANNOTATIONS_ERROR_MSG, field.getName()));
    }
    return (isTrackedField && !isNotTrackedField) || (!isTrackedField && areTrackedAllFields && !isNotTrackedField);
  }

  private void createEntityTrackingFieldData(Object[] previousState, Object[] currentState, String[] propertyNames, EntityTrackingData entityData, Field field) {
    for (int i = 0; i < propertyNames.length; i++) {
      if (propertyNames[i].equals(field.getName())) {
        Object oldValue = previousState.length > 0 ? previousState[i] : null;
        Object newValue = currentState.length > 0 ? currentState[i] : null;

        if (isCollectionOfEntities(field)) {
          return;
        }

        oldValue = isEntity(oldValue) ? retrieveIdFromEntity(oldValue) : oldValue;
        newValue = isEntity(newValue) ? retrieveIdFromEntity(newValue) : newValue;

        if (!Utils.equalsOrCompareEquals(oldValue, newValue)) {
          addOrUpdateFieldData(entityData, new EntityTrackingFieldData(field.getName(), oldValue, newValue), newValue);
        }
      }
    }
  }

  // it's implemented in onCollectionCreate and onCollectionUpdate
  private boolean isCollectionOfEntities(Field field) {
    return field.isAnnotationPresent(OneToMany.class) || field.isAnnotationPresent(ManyToMany.class);
  }

  private void addOrUpdateFieldData(EntityTrackingData entityData, EntityTrackingFieldData fieldData, Object newValue) {
    EntityTrackingData entityTrackingData = changes.get(entityData);
    if (entityTrackingData != null) {
      entityTrackingData.addOrUpdateEntityTrackingField(fieldData, newValue);
    } else {
      entityData.addEntityTrackingField(fieldData);
      changes.put(entityData, entityData);
    }
  }

  private String getFieldNameFromRole(Object owner, String collectionRole) {
    return collectionRole.replace(owner.getClass().getName() + ".", "");
  }

  private boolean isEntity(Object value) {
    return value != null && value.getClass().isAnnotationPresent(Entity.class);
  }

  private Object retrieveIdFromEntity(Object entity) {
    if (entity instanceof HibernateProxy) {
      return ((HibernateProxy) entity).getHibernateLazyInitializer().getIdentifier();
    }
    return retrieveWithReflection(entity);
  }

  private Object retrieveWithReflection(Object entity) {
    if (entity == null) {
      return null;
    }
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
