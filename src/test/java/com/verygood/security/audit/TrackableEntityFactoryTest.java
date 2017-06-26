package com.verygood.security.audit;

import com.verygood.security.audit.data.Action;
import com.verygood.security.audit.entity.Client;

import org.junit.Ignore;
import org.junit.Test;

public class TrackableEntityFactoryTest {
  @Test
  @Ignore //todo: not implemented yet
  public void shouldCreateModifiedAuditEntity() {
    Client client = new Client();

    TrackableEntityFactory.createModifiedEntityAudit(
        1L,
        client,
        new Object[]{1L, },
        new Object[]{},
        new String[]{},
        Action.SAVE
    );
  }
}
