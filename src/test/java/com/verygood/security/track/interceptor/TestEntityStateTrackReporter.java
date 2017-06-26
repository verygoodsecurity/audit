package com.verygood.security.track.interceptor;

import com.verygood.security.track.data.TrackableEntity;
import com.verygood.security.track.EntityStateTrackReporter;

import java.util.ArrayList;
import java.util.List;

public class TestEntityStateTrackReporter implements EntityStateTrackReporter {

  private static final TestEntityStateTrackReporter INSTANCE = new TestEntityStateTrackReporter();
  private static List<TrackableEntity> inserts = new ArrayList<>();
  private static List<TrackableEntity> updates = new ArrayList<>();
  private static List<TrackableEntity> deletes = new ArrayList<>();

  private TestEntityStateTrackReporter() {

  }

  public static TestEntityStateTrackReporter getInstance() {
    return INSTANCE;
  }

  public List<TrackableEntity> getInserts() {
    return inserts;
  }

  public List<TrackableEntity> getUpdates() {
    return updates;
  }

  public List<TrackableEntity> getDeletes() {
    return deletes;
  }

  public void clear() {
    inserts.clear();
    updates.clear();
    deletes.clear();
  }

  @Override
  public void onSave(TrackableEntity trackableEntity) {
    inserts.add(trackableEntity);
  }

  @Override
  public void onUpdate(TrackableEntity trackableEntity) {
    updates.add(trackableEntity);
  }

  @Override
  public void onDelete(TrackableEntity trackableEntity) {
    deletes.add(trackableEntity);
  }
}