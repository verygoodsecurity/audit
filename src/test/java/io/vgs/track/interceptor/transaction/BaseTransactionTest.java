package io.vgs.track.interceptor.transaction;

import io.vgs.track.BaseTest;
import io.vgs.track.interceptor.EntityTrackingTransactionInterceptor;

public abstract class BaseTransactionTest extends BaseTest {

  @Override
  protected String interceptor() {
    return EntityTrackingTransactionInterceptor.class.getName();
  }
}
