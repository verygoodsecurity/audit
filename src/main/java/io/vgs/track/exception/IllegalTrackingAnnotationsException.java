package io.vgs.track.exception;

public class IllegalTrackingAnnotationsException extends RuntimeException {
  public IllegalTrackingAnnotationsException(String fieldName) {
    super("The field " + fieldName + " should have either @Tracked or @NotTracked annotation");
  }
}
