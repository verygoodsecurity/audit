package com.verygood.security.track.interceptor;

import com.verygood.security.track.EntityTrackingListener;
import com.verygood.security.track.data.EntityTrackingData;

import java.util.ArrayList;
import java.util.List;

public class TestEntityStateEntityTrackingListener implements EntityTrackingListener {

  private static final TestEntityStateEntityTrackingListener INSTANCE = new TestEntityStateEntityTrackingListener();
  private static List<EntityTrackingData> inserts = new ArrayList<>();
  private static List<EntityTrackingData> updates = new ArrayList<>();
  private static List<EntityTrackingData> deletes = new ArrayList<>();

  private TestEntityStateEntityTrackingListener() {

  }

  public static TestEntityStateEntityTrackingListener getInstance() {
    return INSTANCE;
  }

  public List<EntityTrackingData> getInserts() {
    return inserts;
  }

  public List<EntityTrackingData> getUpdates() {
    return updates;
  }

  public List<EntityTrackingData> getDeletes() {
    return deletes;
  }

  public void clear() {
    inserts.clear();
    updates.clear();
    deletes.clear();
  }

  @Override
  public void onSave(EntityTrackingData entityTrackingData) {
    System.out.println(entityTrackingData);
    inserts.add(entityTrackingData);
  }

  @Override
  public void onUpdate(EntityTrackingData entityTrackingData) {
    System.out.println(entityTrackingData);
    updates.add(entityTrackingData);
  }

  @Override
  public void onDelete(EntityTrackingData entityTrackingData) {
    System.out.println(entityTrackingData);
    deletes.add(entityTrackingData);
  }
}