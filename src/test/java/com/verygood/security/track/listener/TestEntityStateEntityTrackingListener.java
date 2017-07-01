package com.verygood.security.track.listener;

import com.verygood.security.track.data.Action;
import com.verygood.security.track.data.EntityTrackingData;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class TestEntityStateEntityTrackingListener implements EntityTrackingListener {

  private static List<EntityTrackingData> changes = new ArrayList<>();

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

  public void clear() {
    changes.clear();
  }

  @Override
  public void onEntityChanged(EntityTrackingData entityTrackingData) {
    changes.add(entityTrackingData);
  }
}