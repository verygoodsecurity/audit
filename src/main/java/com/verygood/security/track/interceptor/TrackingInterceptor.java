package com.verygood.security.track.interceptor;

import com.verygood.security.track.listener.EntityTrackingListener;

public interface TrackingInterceptor {
  void setEntityTrackingListener(EntityTrackingListener entityTrackingListener);
}
