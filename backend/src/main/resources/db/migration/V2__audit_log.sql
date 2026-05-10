-- V2: Audit log table

CREATE TABLE audit_log (
    id          BIGSERIAL PRIMARY KEY,
    entity_type VARCHAR(100) NOT NULL,
    entity_id   BIGINT,
    action      VARCHAR(50)  NOT NULL,
    performed_by VARCHAR(100),
    details     TEXT,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_audit_entity   ON audit_log(entity_type, entity_id);
CREATE INDEX idx_audit_action   ON audit_log(action);
CREATE INDEX idx_audit_created  ON audit_log(created_at);
