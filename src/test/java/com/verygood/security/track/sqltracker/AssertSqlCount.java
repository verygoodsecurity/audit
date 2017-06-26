package com.verygood.security.track.sqltracker;

import static com.verygood.security.track.sqltracker.QueryCountInfoHolder.getQueryInfo;

public class AssertSqlCount {

  public static void reset() {
    getQueryInfo().clear();
  }

  public static void assertSqlCount(int expectedAllCount) {
    assertSqlCount("all", expectedAllCount, getQueryInfo().countAll());
  }

  public static void assertSelectCount(int expectedSelectCount) {
    assertSqlCount("select", expectedSelectCount, getQueryInfo().getSelectCount());
  }

  public static void assertUpdateCount(int expectedUpdateCount) {
    assertSqlCount("update", expectedUpdateCount, getQueryInfo().getUpdateCount());
  }

  public static void assertInsertCount(int expectedInsertCount) {
    assertSqlCount("insert", expectedInsertCount, getQueryInfo().getInsertCount());
  }

  public static void assertDeleteCount(int expectedDeleteCount) {
    assertSqlCount("delete", expectedDeleteCount, getQueryInfo().getDeleteCount());

  }

  private static void assertSqlCount(String statement, int expectedCount, int actualCount) {
    if (expectedCount != actualCount) {
      throw new SqlCountMismatchException(statement, expectedCount, actualCount);
    }
  }

}
