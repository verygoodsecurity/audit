package com.verygood.security.track.sqltracker;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

public class SqlCountTrackerDatasource implements DataSource {
  private DataSource realDataSource;

  public SqlCountTrackerDatasource(DataSource realDataSource) {
    this.realDataSource = realDataSource;
  }

  // Decorated

  @Override
  public Connection getConnection() throws SQLException {
    final Connection connection = realDataSource.getConnection();
    return new SqlCountTrackerConnection(connection);
  }

  @Override
  public Connection getConnection(String username, String password) throws SQLException {
    final Connection connection = realDataSource.getConnection(username, password);
    return new SqlCountTrackerConnection(connection);
  }

  // ---------------------------------------------------------------------

  @Override
  public <T> T unwrap(Class<T> iface) throws SQLException {
    return realDataSource.unwrap(iface);
  }

  @Override
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    return realDataSource.isWrapperFor(iface);
  }

  @Override
  public PrintWriter getLogWriter() throws SQLException {
    return realDataSource.getLogWriter();
  }

  @Override
  public void setLogWriter(PrintWriter out) throws SQLException {
    realDataSource.setLogWriter(out);
  }

  @Override
  public int getLoginTimeout() throws SQLException {
    return realDataSource.getLoginTimeout();
  }

  @Override
  public void setLoginTimeout(int seconds) throws SQLException {
    realDataSource.setLoginTimeout(seconds);
  }

  @Override
  public Logger getParentLogger() throws SQLFeatureNotSupportedException {
    return realDataSource.getParentLogger();
  }

}
