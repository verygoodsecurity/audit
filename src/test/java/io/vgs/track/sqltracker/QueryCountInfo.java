package io.vgs.track.sqltracker;

public class QueryCountInfo {

  private int selectCount;
  private int insertCount;
  private int updateCount;
  private int deleteCount;

  void incrementSelectCount() {
    selectCount++;
  }

  void incrementInsertCount() {
    insertCount++;
  }

  void incrementUpdateCount() {
    updateCount++;
  }

  void incrementDeleteCount() {
    deleteCount++;
  }

  public void clear() {
    selectCount = 0;
    insertCount = 0;
    updateCount = 0;
    deleteCount = 0;
  }

  public int countAll() {
    return selectCount + insertCount + updateCount + deleteCount;
  }

  public int getSelectCount() {
    return selectCount;
  }

  public int getInsertCount() {
    return insertCount;
  }

  public int getUpdateCount() {
    return updateCount;
  }

  public int getDeleteCount() {
    return deleteCount;
  }
}
