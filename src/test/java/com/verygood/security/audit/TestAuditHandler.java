package com.verygood.security.audit;

import java.util.ArrayList;
import java.util.List;

public class TestAuditHandler implements AuditHandler {

  private static final TestAuditHandler INSTANCE = new TestAuditHandler();
  private static List<ModifiedEntityAudit> inserts = new ArrayList<>();
  private static List<ModifiedEntityAudit> updates = new ArrayList<>();
  private static List<ModifiedEntityAudit> deletes = new ArrayList<>();

  private TestAuditHandler() {

  }

  public static TestAuditHandler getInstance() {
    return INSTANCE;
  }

  public List<ModifiedEntityAudit> getInserts() {
    return inserts;
  }

  public List<ModifiedEntityAudit> getUpdates() {
    return updates;
  }

  public List<ModifiedEntityAudit> getDeletes() {
    return deletes;
  }

  public void clear() {
    inserts.clear();
    updates.clear();
    deletes.clear();
  }

  @Override
  public void onSave(ModifiedEntityAudit modifiedEntityAudit) {
    inserts.add(modifiedEntityAudit);
  }

  @Override
  public void onUpdate(ModifiedEntityAudit modifiedEntityAudit) {
    updates.add(modifiedEntityAudit);
  }

  @Override
  public void onDelete(ModifiedEntityAudit modifiedEntityAudit) {
    deletes.add(modifiedEntityAudit);
  }
}