package com.verygood.security.track;

import com.verygood.security.track.data.EntityTrackingData;

public interface EntityTrackingListener {
  void onSave(EntityTrackingData modifiedEntity);

  void onUpdate(EntityTrackingData modifiedEntity);

  void onDelete(EntityTrackingData modifiedEntity);
}
