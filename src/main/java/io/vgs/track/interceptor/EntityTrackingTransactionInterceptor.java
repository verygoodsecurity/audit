package io.vgs.track.interceptor;

import static java.util.stream.Collectors.toList;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.Hibernate;
import org.hibernate.Transaction;
import org.hibernate.collection.internal.AbstractPersistentCollection;
import org.hibernate.collection.internal.PersistentMap;
import org.hibernate.collection.internal.PersistentSet;
import org.hibernate.engine.spi.CollectionEntry;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vgs.track.data.Action;
import io.vgs.track.data.EntityTrackingData;
import io.vgs.track.data.EntityTrackingFieldData;
import io.vgs.track.exception.IllegalTrackingAnnotationsException;
import io.vgs.track.listener.EntityTrackingListener;
import io.vgs.track.meta.NotTracked;
import io.vgs.track.meta.Trackable;
import io.vgs.track.meta.Tracked;
import io.vgs.track.utils.FieldUtils;
import io.vgs.track.utils.Utils;

public class EntityTrackingTransactionInterceptor extends EmptyInterceptor implements EntityTrackingListenerAware {

    private static final Logger LOG = LoggerFactory.getLogger(EntityTrackingTransactionInterceptor.class);
    private static final String TRACK_AND_NOT_TRACK_ANNOTATIONS_ERROR_MSG = "The field %s should have either @Tracked or @NotTracked annotation";
    private static final String PERSISTENT_MAP_TRACKING_IS_NOT_SUPPORTED_YET = "PersistentMap tracking is not supported yet";

    private EntityTrackingListener entityTrackingListener;
    private final Map<EntityTrackingData, EntityTrackingData> changes = new HashMap<>();

    private static Class<?> getClassForHibernateObject(final Object object) {
        if (object instanceof HibernateProxy) {
            final LazyInitializer lazyInitializer = ((HibernateProxy) object).getHibernateLazyInitializer();
            return lazyInitializer.getPersistentClass();
        } else {
            return object.getClass();
        }
    }

    @Override
    public void onDelete(final Object entity, final Serializable id, final Object[] state, final String[] propertyNames,
            final Type[] types) {
        super.onDelete(entity, id, state, propertyNames, types);
        createEntityTrackingData(id, entity, state, new Object[] {}, propertyNames, Action.DELETED);
    }

    @Override
    public boolean onFlushDirty(final Object entity, final Serializable id, final Object[] currentState,
            final Object[] previousState, final String[] propertyNames, final Type[] types) {
        super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
        createEntityTrackingData(id, entity, previousState, currentState, propertyNames, Action.UPDATED);
        return false;
    }

    @Override
    public boolean onSave(final Object entity, final Serializable id, final Object[] state,
            final String[] propertyNames, final Type[] types) {
        super.onSave(entity, id, state, propertyNames, types);
        createEntityTrackingData(id, entity, new Object[] {}, state, propertyNames, Action.CREATED);
        return false;
    }

    @Override
    public void beforeTransactionCompletion(final Transaction tx) {
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
    public void onCollectionRecreate(final Object collection, final Serializable key) throws CallbackException {
        super.onCollectionRecreate(collection, key);

        if (!(collection instanceof AbstractPersistentCollection)) {
            return;
        }
        // not implemented yet
        if (collection instanceof PersistentMap) {
            LOG.warn(PERSISTENT_MAP_TRACKING_IS_NOT_SUPPORTED_YET);
            return;
        }

        final AbstractPersistentCollection newValues = (AbstractPersistentCollection) collection;

        final Object owner = newValues.getOwner();
        if (!owner.getClass().isAnnotationPresent(Trackable.class)) {
            return;
        }

        final CollectionEntry collectionEntry = newValues.getSession().getPersistenceContext()
                .getCollectionEntry(newValues);
        final String collectionRole = collectionEntry.getCurrentPersister().getRole();
        final String collectionFieldName = getFieldNameFromRole(owner, collectionRole);

        final Field collectionField = FieldUtils.getDeclaredField(owner.getClass(), collectionFieldName, true);
        final boolean areTrackedAllFields = owner.getClass().isAnnotationPresent(Tracked.class);
        if (!validateAnnotations(areTrackedAllFields, collectionField)) {
            return;
        }

        final Collection<?> newCollection = (Collection<?>) collection;

        final boolean isCollectionOfEntities = isCollectionOfEntities(collectionField);
        final Object oldValue = null;
        final Object newValue = isCollectionOfEntities
                ? newCollection.stream().map(this::retrieveIdFromEntity).collect(toList())
                : newCollection;

        final EntityTrackingData entityData = new EntityTrackingData(key, owner.getClass(), Action.CREATED);
        final EntityTrackingFieldData fieldData = new EntityTrackingFieldData(collectionFieldName, oldValue, newValue);
        addOrUpdateFieldData(entityData, fieldData, newValue);
    }

    @Override
    public void onCollectionUpdate(final Object collection, final Serializable key) throws CallbackException {
        super.onCollectionUpdate(collection, key);

        // do not touch lazy collections, like PersistentBag
        // for performance sake entity.getPersistentBag().add(element) does not trigger
        // sql select to fetch entire collection
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

        final AbstractPersistentCollection newValues = (AbstractPersistentCollection) collection;
        final Object owner = newValues.getOwner();
        if (!owner.getClass().isAnnotationPresent(Trackable.class)) {
            return;
        }

        final String collectionFieldName = getFieldNameFromRole(owner, newValues.getRole());
        final Field collectionField = FieldUtils.getDeclaredField(owner.getClass(), collectionFieldName, true);
        final boolean areTrackedAllFields = owner.getClass().isAnnotationPresent(Tracked.class);
        if (!validateAnnotations(areTrackedAllFields, collectionField)) {
            return;
        }

        final Collection<?> oldCollection = (newValues instanceof PersistentSet)
                ? ((Map<?, ?>) newValues.getStoredSnapshot()).keySet()
                : (Collection<?>) newValues.getStoredSnapshot();
        final Collection<?> newCollection = (Collection<?>) collection;

        final boolean isCollectionOfEntities = isCollectionOfEntities(collectionField);
        final Collection<?> oldValue = isCollectionOfEntities
                ? oldCollection.stream().map(this::retrieveIdFromEntity).collect(toList())
                : oldCollection;
        final Collection<?> newValue = isCollectionOfEntities
                ? newCollection.stream().map(this::retrieveIdFromEntity).collect(toList())
                : newCollection;

        if (!Utils.collectionsEqual(oldValue, newValue)) {
            addOrUpdateFieldData(new EntityTrackingData(key, owner.getClass(), Action.UPDATED),
                    new EntityTrackingFieldData(collectionFieldName, oldValue, newValue), newValue);
        }
    }

    private void createEntityTrackingData(final Serializable id, final Object entity, final Object[] previousState,
            final Object[] currentState, final String[] propertyNames, final Action action) {
        if (!entity.getClass().isAnnotationPresent(Trackable.class)) {
            return;
        }
        final EntityTrackingData entityData = new EntityTrackingData(id, entity.getClass(), action);

        final boolean areTrackedAllFields = entity.getClass().isAnnotationPresent(Tracked.class);
        for (final Field field : entity.getClass().getDeclaredFields()) {
            if (validateAnnotations(areTrackedAllFields, field)) {
                createEntityTrackingFieldData(previousState, currentState, propertyNames, entityData, field);
            }
        }
    }

    private boolean validateAnnotations(final boolean areTrackedAllFields, final Field field) {
        final boolean isTrackedField = field.isAnnotationPresent(Tracked.class);
        final boolean isNotTrackedField = field.isAnnotationPresent(NotTracked.class);

        if (isTrackedField && isNotTrackedField) {
            throw new IllegalTrackingAnnotationsException(
                    String.format(TRACK_AND_NOT_TRACK_ANNOTATIONS_ERROR_MSG, field.getName()));
        }
        return (isTrackedField && !isNotTrackedField) || (!isTrackedField && areTrackedAllFields && !isNotTrackedField);
    }

    private void createEntityTrackingFieldData(final Object[] previousState, final Object[] currentState,
            final String[] propertyNames, final EntityTrackingData entityData, final Field field) {
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
                    addOrUpdateFieldData(entityData, new EntityTrackingFieldData(field.getName(),
                            getFieldValue(oldValue, field), getFieldValue(newValue, field)), newValue);
                }
            }
        }
    }

    private Object getFieldValue(final Object value, final Field field) {
        return Optional.ofNullable(field.getAnnotation(Tracked.class)).filter(Tracked::replace)
                .map(t -> (Object) t.replaceWith()).orElse(value);
    }

    // it's implemented in onCollectionCreate and onCollectionUpdate
    private boolean isCollectionOfEntities(final Field field) {
        return field.isAnnotationPresent(OneToMany.class) || field.isAnnotationPresent(ManyToMany.class);
    }

    private void addOrUpdateFieldData(final EntityTrackingData entityData, final EntityTrackingFieldData fieldData,
            final Object newValue) {
        final EntityTrackingData entityTrackingData = changes.get(entityData);
        if (entityTrackingData != null) {
            entityTrackingData.addOrUpdateEntityTrackingField(fieldData, newValue);
        } else {
            entityData.addEntityTrackingField(fieldData);
            changes.put(entityData, entityData);
        }
    }

    private String getFieldNameFromRole(final Object owner, final String collectionRole) {
        return collectionRole.replace(owner.getClass().getName() + ".", "");
    }

    private boolean isEntity(final Object value) {
        return value != null && getClassForHibernateObject(value).isAnnotationPresent(Entity.class);
    }

    private Object retrieveIdFromEntity(final Object entity) {
        if (entity instanceof HibernateProxy) {
            return ((HibernateProxy) entity).getHibernateLazyInitializer().getIdentifier();
        }
        return retrieveWithReflection(entity);
    }

    private Object retrieveWithReflection(final Object entity) {
        if (entity == null) {
            return null;
        }
        for (final Field field : FieldUtils.getAllFieldsList(entity.getClass())) {
            if (field.isAnnotationPresent(Id.class)) {
                try {
                    field.setAccessible(true);
                    return field.get(entity);
                } catch (final IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }

    @Override
    public void setEntityTrackingListener(final EntityTrackingListener entityTrackingListener) {
        this.entityTrackingListener = entityTrackingListener;
    }
}
