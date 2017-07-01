package com.verygood.security.track.exception;

public class IllegalEntityTrackingInterceptorException extends RuntimeException{
  public IllegalEntityTrackingInterceptorException() {
    super("Please provide EntityTrackingListener implementation: EntityTrackingTransactionInterceptor.setEntityTrackingListener");
  }
}
