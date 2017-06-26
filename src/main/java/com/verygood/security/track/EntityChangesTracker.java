package com.verygood.security.track;

public interface EntityChangesTracker {
  void onSave(ChangedEntity changedEntity);

  void onUpdate(ChangedEntity changedEntity);

  void onDelete(ChangedEntity changedEntity);
}
