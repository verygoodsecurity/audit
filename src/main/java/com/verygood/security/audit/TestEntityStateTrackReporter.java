package com.verygood.security.audit;

import com.verygood.security.audit.data.TrackableEntity;

public interface TestEntityStateTrackReporter {
  void onSave(TrackableEntity modifiedEntity);

  void onUpdate(TrackableEntity modifiedEntity);

  void onDelete(TrackableEntity modifiedEntity);
}
