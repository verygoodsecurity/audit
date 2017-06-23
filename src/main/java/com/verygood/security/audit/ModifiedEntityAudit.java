package com.verygood.security.audit;

import com.google.common.base.Objects;

import java.io.Serializable;
import java.util.List;

public class ModifiedEntityAudit {
  private final Serializable id;
  private final Class clazz;
  private final AuditAction action;
  private final List<ModifiedEntityAuditField> modifiedFields;

  public ModifiedEntityAudit(Serializable id, Class clazz, AuditAction action, List<ModifiedEntityAuditField> modifiedFields) {
    this.id = id;
    this.clazz = clazz;
    this.action = action;
    this.modifiedFields = modifiedFields;
  }

  public Serializable getId() {
    return id;
  }

  public Class getClassName() {
    return clazz;
  }

  public List<ModifiedEntityAuditField> getModifiedFields() {
    return modifiedFields;
  }

  @Override
  public String toString() {
    return "ModifiedEntityAudit{" +
        "id=" + id +
        ", clazz='" + clazz + '\'' +
        ", action=" + action +
        ", modifiedFields=" + modifiedFields +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ModifiedEntityAudit)) return false;
    ModifiedEntityAudit that = (ModifiedEntityAudit) o;
    return Objects.equal(this.id, that.id)
        && Objects.equal(this.clazz, that.clazz)
        && action == that.action;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id, clazz, action);
  }
}
