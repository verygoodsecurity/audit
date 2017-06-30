package com.verygood.security.track.exception;

public class IllegalEntityTrackingFieldAnnotationException extends RuntimeException {
  public IllegalEntityTrackingFieldAnnotationException(String fieldName) {
    super("The field " + fieldName + " should have either @Tracked or @NotTracked annotation");
  }
}
