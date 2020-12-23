package io.vgs.track.listener;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import io.vgs.track.data.Action;
import io.vgs.track.data.EntityTrackingData;
import io.vgs.track.data.EntityTrackingFieldData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class TestEntityStateEntityTrackingListener implements EntityTrackingListener {

  private static final List<EntityTrackingData> changes = new ArrayList<>();

  public List<EntityTrackingData> getInserts() {
    return changes.stream()
        .filter(entity -> entity.getAction() == Action.CREATED)
        .collect(toList());
  }

  public List<EntityTrackingData> getUpdates() {
    return changes.stream()
        .filter(entity -> entity.getAction() == Action.UPDATED)
        .collect(toList());
  }

  public List<EntityTrackingData> getDeletes() {
    return changes.stream()
        .filter(entity -> entity.getAction() == Action.DELETED)
        .collect(toList());
  }

  public EntityTrackingFieldData getInsertedField(String fieldName) {
    return getField(getInserts(), fieldName);
  }

  public EntityTrackingFieldData getUpdatedField(String fieldName) {
    return getField(getUpdates(), fieldName);
  }

  public EntityTrackingFieldData getDeletedField(String fieldName) {
    return getField(getDeletes(), fieldName);
  }

  private EntityTrackingFieldData getField(List<EntityTrackingData> data, String fieldName) {
    return data.stream()
        .map(EntityTrackingData::getEntityTrackingFields)
        .flatMap(Collection::stream)
        .collect(toMap(EntityTrackingFieldData::getName, Function.identity(), (k1, k2) -> k1))
        .get(fieldName);
  }

  public List<EntityTrackingFieldData> getInsertedFields(Class clazz) {
    return getInserts().stream()
        .collect(toMap(EntityTrackingData::getClazz, EntityTrackingData::getEntityTrackingFields))
        .getOrDefault(clazz, Collections.emptyList());
  }

  public List<EntityTrackingFieldData> getUpdatedFields(Class clazz) {
    return getUpdates().stream()
        .collect(toMap(EntityTrackingData::getClazz, EntityTrackingData::getEntityTrackingFields))
        .getOrDefault(clazz, Collections.emptyList());
  }

  public List<EntityTrackingFieldData> getDeletedFields(Class clazz) {
    return getDeletes().stream()
        .collect(toMap(EntityTrackingData::getClazz, EntityTrackingData::getEntityTrackingFields))
        .getOrDefault(clazz, Collections.emptyList());
  }


  public void clear() {
    changes.clear();
  }

  @Override
  public void onEntityChanged(EntityTrackingData entityTrackingData) {
    changes.add(entityTrackingData);
  }

  public EntityTrackingData getUpdatedEntity(Long id) {
    return getUpdates().stream()
        .filter(data -> data.getId().equals(id))
        .findFirst()
        .orElse(null);
  }
}