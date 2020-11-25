package io.vgs.track.listener;

import io.vgs.track.data.EntityTrackingData;

public interface EntityTrackingListener {

    void onEntityChanged(EntityTrackingData entityTrackingData);
}
