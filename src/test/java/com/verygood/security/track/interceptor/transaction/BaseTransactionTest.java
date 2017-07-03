package com.verygood.security.track.interceptor.transaction;

import com.verygood.security.track.BaseTest;
import com.verygood.security.track.interceptor.EntityTrackingTransactionInterceptor;

public abstract class BaseTransactionTest extends BaseTest {

  @Override
  protected String interceptor() {
    return EntityTrackingTransactionInterceptor.class.getName();
  }
}
