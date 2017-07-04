package io.vgs.track.interceptor;

import io.vgs.track.listener.EntityTrackingListener;

public interface EntityTrackingListenerAware {
  void setEntityTrackingListener(EntityTrackingListener entityTrackingListener);
}
