package com.verygood.security.audit;

import org.hibernate.EmptyInterceptor;

import java.io.Serializable;

public class AuditInterceptor extends EmptyInterceptor {
  // can't use constructor injection cause interceptor is created using new operator
  private AuditHandler auditHandler;

  public void setAuditHandler(AuditHandler auditHandler) {
    this.auditHandler = auditHandler;
    checkAuditHandler();
  }

  @Override
  public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, org.hibernate.type.Type[] types) {
    checkAuditHandler();
    if (entity.getClass().isAnnotationPresent(Trackable.class)) {
      auditHandler.onSave(
          AuditFactory.createModifiedEntityAudit(
              id,
              entity,
              new Object[]{},
              state,
              propertyNames,
              AuditAction.SAVE
          )
      );
    }
    return false;
  }

  @Override
  public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, org.hibernate.type.Type[] types) {
    checkAuditHandler();
    if (entity.getClass().isAnnotationPresent(Trackable.class)) {
      auditHandler.onUpdate(
          AuditFactory.createModifiedEntityAudit(
              id,
              entity,
              previousState,
              currentState,
              propertyNames,
              AuditAction.UPDATE
          )
      );
    }
    return false;
  }

  @Override
  public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, org.hibernate.type.Type[] types) {
    checkAuditHandler();
    if (entity.getClass().isAnnotationPresent(Trackable.class)) {
      auditHandler.onDelete(
          AuditFactory.createModifiedEntityAudit(
              id,
              entity,
              state,
              new Object[]{},
              propertyNames,
              AuditAction.DELETE
          )
      );
    }
  }

  private void checkAuditHandler() {
    if (auditHandler == null) {
      throw new IllegalStateException("Please provide AuditHandler");
    }
  }
}
