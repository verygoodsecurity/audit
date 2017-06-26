package com.verygood.security.track;

import com.verygood.security.track.entity.Client;

import org.junit.Ignore;
import org.junit.Test;

public class EntityChangesFactoryTest {
  @Test
  @Ignore //todo: not implemented yet
  public void shouldCreateChangedEntity() {
    Client client = new Client(null);

    EntityChangesFactory.createChangedEntity(
        1L,
        client,
        new Object[]{1L, },
        new Object[]{},
        new String[]{},
        Action.SAVE
    );
  }
}
