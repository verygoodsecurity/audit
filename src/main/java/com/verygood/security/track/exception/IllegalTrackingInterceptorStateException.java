package com.verygood.security.track.exception;

public class IllegalTrackingInterceptorStateException extends RuntimeException{
  public IllegalTrackingInterceptorStateException() {
    super("Please provide EntityStateTrackReporter implementation: TrackingEntityStateChangesInterceptor.setEntityStateTrackReporter");
  }
}
