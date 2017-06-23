package com.verygood.security.audit.sqltracker;

public interface QueryHandler {
  void handleSql(String sql);
}
