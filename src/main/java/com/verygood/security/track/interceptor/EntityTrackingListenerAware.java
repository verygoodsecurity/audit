package com.verygood.security.track.interceptor;

import com.verygood.security.track.listener.EntityTrackingListener;

public interface EntityTrackingListenerAware {
  void setEntityTrackingListener(EntityTrackingListener entityTrackingListener);
}
