package com.verygood.security.track;

import com.verygood.security.track.data.Action;
import com.verygood.security.track.entity.Client;

import org.junit.Ignore;
import org.junit.Test;

public class EntityTrackingDataFactoryTest {
  @Test
  @Ignore //todo: not implemented yet
  public void shouldCreateTrackableEntity() {
    Client client = new Client();

    EntityTrackingFactory.createTrackableEntity(
        1L,
        client,
        new Object[]{1L, },
        new Object[]{},
        new String[]{},
        Action.CREATED
    );
  }
}
