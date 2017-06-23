package com.verygood.security.audit;

public interface AuditHandler {
  void onSave(ModifiedEntityAudit modifiedEntity);

  void onUpdate(ModifiedEntityAudit modifiedEntity);

  void onDelete(ModifiedEntityAudit modifiedEntity);
}
