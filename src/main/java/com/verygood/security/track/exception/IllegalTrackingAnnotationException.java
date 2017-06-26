package com.verygood.security.track.exception;

public class IllegalTrackingAnnotationException extends RuntimeException {
  public IllegalTrackingAnnotationException(String fieldName) {
    super("The field " + fieldName + " should have either @Tracked or @NotTracked annotation");
  }
}
