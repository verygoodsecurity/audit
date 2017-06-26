package com.verygood.security.track;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.Id;

import static java.util.stream.Collectors.toList;

class EntityChangesFactory {
  private EntityChangesFactory() {

  }

  static ChangedEntity createChangedEntity(Serializable id, Object entity, Object[] previousState, Object[] currentState, String[] propertyNames, Action action) {
    List<ChangedEntityField> modifiedFields = createModifiedFields(entity, previousState, currentState, propertyNames);
    return new ChangedEntity(id, entity.getClass(), action, modifiedFields);
  }

  private static List<ChangedEntityField> createModifiedFields(Object entity,
                                                               Object[] previousState,
                                                               Object[] currentState,
                                                               String[] propertyNames
  ) {
    List<ChangedEntityField> changedEntityFields = new ArrayList<>();
    boolean trackAllFields = entity.getClass().isAnnotationPresent(Tracked.class);
    for (Field field : entity.getClass().getDeclaredFields()) {
      Tracked tracked = field.getAnnotation(Tracked.class);
      if (trackAllFields || (tracked != null && !tracked.exclude())) {
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

            changedEntityFields.add(new ChangedEntityField(field.getName(), oldValue, newValue));
          }
        }
      }
    }
    return changedEntityFields;
  }

  private static boolean isEntity(Object oldValue) {
    return oldValue.getClass().isAnnotationPresent(Entity.class);
  }

  private static Object retrieveCollectionIds(Object oldValue) {
    Collection elements = (Collection) oldValue;
    //noinspection unchecked
    boolean isEntities = elements.stream().allMatch(EntityChangesFactory::isEntity);
    return isEntities ? entitiesToIds(elements) : oldValue;
  }

  private static Object entitiesToIds(Collection elements) {
    //noinspection unchecked
    return elements.stream()
        .map(EntityChangesFactory::retrieveId)
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
    List<Field> fields = FieldUtils.getAllFieldsList(entity.getClass());
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
