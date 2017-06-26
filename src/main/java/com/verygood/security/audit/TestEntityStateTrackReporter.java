package com.verygood.security.audit;

public interface TestEntityStateTrackReporter {
  void onSave(TrackableEntity modifiedEntity);

  void onUpdate(TrackableEntity modifiedEntity);

  void onDelete(TrackableEntity modifiedEntity);
}
