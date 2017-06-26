package com.verygood.security.track.interceptor;

import com.verygood.security.track.ChangedEntity;
import com.verygood.security.track.EntityChangesTracker;

import java.util.ArrayList;
import java.util.List;

public class TestEntityChangesTracker implements EntityChangesTracker {

  private static final TestEntityChangesTracker INSTANCE = new TestEntityChangesTracker();
  private static List<ChangedEntity> inserts = new ArrayList<>();
  private static List<ChangedEntity> updates = new ArrayList<>();
  private static List<ChangedEntity> deletes = new ArrayList<>();

  private TestEntityChangesTracker() {

  }

  public static TestEntityChangesTracker getInstance() {
    return INSTANCE;
  }

  public List<ChangedEntity> getInserts() {
    return inserts;
  }

  public List<ChangedEntity> getUpdates() {
    return updates;
  }

  public List<ChangedEntity> getDeletes() {
    return deletes;
  }

  public void clear() {
    inserts.clear();
    updates.clear();
    deletes.clear();
  }

  @Override
  public void onSave(ChangedEntity changedEntity) {
    inserts.add(changedEntity);
  }

  @Override
  public void onUpdate(ChangedEntity changedEntity) {
    updates.add(changedEntity);
  }

  @Override
  public void onDelete(ChangedEntity changedEntity) {
    deletes.add(changedEntity);
  }
}