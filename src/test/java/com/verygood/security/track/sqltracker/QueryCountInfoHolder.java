package com.verygood.security.track.sqltracker;

public class QueryCountInfoHolder {
  private static ThreadLocal<QueryCountInfo> queryInfoHolder = new ThreadLocal<QueryCountInfo>() {
    @Override
    protected QueryCountInfo initialValue() {
      return new QueryCountInfo();
    }
  };

  public static QueryCountInfo getQueryInfo() {
    return queryInfoHolder.get();
  }

  public static void clear() {
    queryInfoHolder.get().clear();
  }
}
