package com.verygood.security.track;

import com.verygood.security.track.data.TrackableEntity;

public interface TestEntityStateTrackReporter {
  void onSave(TrackableEntity modifiedEntity);

  void onUpdate(TrackableEntity modifiedEntity);

  void onDelete(TrackableEntity modifiedEntity);
}
