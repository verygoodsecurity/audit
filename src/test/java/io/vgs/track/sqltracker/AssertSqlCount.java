package io.vgs.track.sqltracker;

public class AssertSqlCount {

  public static void reset() {
    QueryCountInfoHolder.getQueryInfo().clear();
  }

  public static void assertSqlCount(int expectedAllCount) {
    assertSqlCount("all", expectedAllCount, QueryCountInfoHolder.getQueryInfo().countAll());
  }

  public static void assertSelectCount(int expectedSelectCount) {
    assertSqlCount("select", expectedSelectCount, QueryCountInfoHolder.getQueryInfo().getSelectCount());
  }

  public static void assertUpdateCount(int expectedUpdateCount) {
    assertSqlCount("update", expectedUpdateCount, QueryCountInfoHolder.getQueryInfo().getUpdateCount());
  }

  public static void assertInsertCount(int expectedInsertCount) {
    assertSqlCount("insert", expectedInsertCount, QueryCountInfoHolder.getQueryInfo().getInsertCount());
  }

  public static void assertDeleteCount(int expectedDeleteCount) {
    assertSqlCount("delete", expectedDeleteCount, QueryCountInfoHolder.getQueryInfo().getDeleteCount());

  }

  private static void assertSqlCount(String statement, int expectedCount, int actualCount) {
    if (expectedCount != actualCount) {
      throw new SqlCountMismatchException(statement, expectedCount, actualCount);
    }
  }

}
