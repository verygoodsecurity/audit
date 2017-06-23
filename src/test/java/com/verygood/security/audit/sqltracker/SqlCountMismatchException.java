package com.verygood.security.audit.sqltracker;

class SqlCountMismatchException extends RuntimeException {
  SqlCountMismatchException(String statement, int expectedCount, int actualCount) {
    super("Expected " + statement + " query count : " + expectedCount + ", but was : " + actualCount);
  }
}
