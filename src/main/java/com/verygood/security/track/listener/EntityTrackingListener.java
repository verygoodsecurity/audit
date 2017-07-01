package com.verygood.security.track.listener;

import com.verygood.security.track.data.EntityTrackingData;

public interface EntityTrackingListener {
  void onEntityChanged(EntityTrackingData entityTrackingData);
}
