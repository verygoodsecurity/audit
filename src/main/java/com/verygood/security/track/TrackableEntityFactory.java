package com.verygood.security.track;

import com.verygood.security.track.data.Action;
import com.verygood.security.track.data.TrackableEntity;
import com.verygood.security.track.data.TrackableEntityField;
import com.verygood.security.track.exception.IllegalTrackingAnnotationException;
import com.verygood.security.track.meta.NotTracked;
import com.verygood.security.track.meta.Trackable;
import com.verygood.security.track.meta.Tracked;

import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;

import static java.util.stream.Collectors.toList;

class TrackableEntityFactory {
  private TrackableEntityFactory() {

  }

  static Optional<TrackableEntity> createTrackableEntity(Serializable id, Object entity, Object[] previousState, Object[] currentState, String[] propertyNames, Action action) {
    if (!entity.getClass().isAnnotationPresent(Trackable.class)) {
      return Optional.empty();
    }
    Set<TrackableEntityField> modifiedFields = createModifiedFields(entity, previousState, currentState, propertyNames);
    return Optional.of(new TrackableEntity(id, entity.getClass(), action, modifiedFields));
  }

  private static Set<TrackableEntityField> createModifiedFields(Object entity,
                                                                Object[] previousState,
                                                                Object[] currentState,
                                                                String[] propertyNames
  ) {
    Set<TrackableEntityField> trackableEntityFields = new HashSet<>();
    boolean areTrackedAllFields = entity.getClass().isAnnotationPresent(Tracked.class);
    for (Field field : entity.getClass().getDeclaredFields()) {
      boolean isTrackedField = field.isAnnotationPresent(Tracked.class);
      boolean isNotTrackedField = field.isAnnotationPresent(NotTracked.class);

      if (isTrackedField && isNotTrackedField) {
        throw new IllegalTrackingAnnotationException("The field " + field.getName() + " should have either @Tracked or @NotTracked annotation");
      }

      if ((isTrackedField && !isNotTrackedField) || (!isTrackedField && areTrackedAllFields && !isNotTrackedField)) {
        for (int i = 0; i < propertyNames.length; i++) {
          if (propertyNames[i].equals(field.getName())) {
            Object oldValue = previousState.length > 0 ? previousState[i] : null;
            Object newValue = currentState.length > 0 ? currentState[i] : null;

            // don't touch lazy old fields
            if (!Hibernate.isInitialized(oldValue)) {
              continue;
            }

            // transform relationships to ids
            if (oldValue instanceof Collection) {
              oldValue = retrieveCollectionIds(oldValue);
            }

            if (newValue instanceof Collection) {
              newValue = retrieveCollectionIds(newValue);
            }

            if (oldValue != null && isEntity(oldValue)) {
              oldValue = retrieveId(oldValue);
            }

            if (newValue != null && isEntity(newValue)) {
              newValue = retrieveId(newValue);
            }

            boolean areEqual = Utils.areEqualOrCompareEqual(oldValue, newValue)
                || Utils.areEqualEmptyCollection(oldValue, newValue);

            if (areEqual) {
              continue;
            }

            trackableEntityFields.add(new TrackableEntityField(field.getName(), oldValue, newValue));
          }
        }
      }
    }
    return trackableEntityFields;
  }

  private static boolean isEntity(Object oldValue) {
    return oldValue.getClass().isAnnotationPresent(Entity.class);
  }

  private static Object retrieveCollectionIds(Object oldValue) {
    Collection elements = (Collection) oldValue;
    //noinspection unchecked
    boolean isEntities = elements.stream().allMatch(TrackableEntityFactory::isEntity);
    return isEntities ? entitiesToIds(elements) : oldValue;
  }

  private static Object entitiesToIds(Collection elements) {
    //noinspection unchecked
    return elements.stream()
        .map(TrackableEntityFactory::retrieveId)
        .filter(Objects::nonNull)
        .collect(toList());
  }

  private static Object retrieveId(Object entity) {
    if (entity == null) {
      throw new IllegalArgumentException("Passed entity cannot be null");
    }
    if (entity instanceof HibernateProxy) {
      return ((HibernateProxy) entity).getHibernateLazyInitializer().getIdentifier();
    }
    return retrieveWithReflection(entity);
  }

  private static Object retrieveWithReflection(Object entity) {
    Field[] fields = entity.getClass().getDeclaredFields();
    for (Field field : fields) {
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
}
