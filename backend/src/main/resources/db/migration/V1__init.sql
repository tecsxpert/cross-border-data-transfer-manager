-- V1: Core tables for Tool-135 Cross-Border Data Transfer Manager

CREATE TABLE users (
    id          BIGSERIAL PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    email       VARCHAR(255) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    role        VARCHAR(20)  NOT NULL DEFAULT 'USER',
    active      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email    ON users(email);

-- -------------------------------------------------------

CREATE TABLE data_transfers (
    id                    BIGSERIAL PRIMARY KEY,
    title                 VARCHAR(255) NOT NULL,
    description           TEXT,
    source_country        VARCHAR(100) NOT NULL,
    destination_country   VARCHAR(100) NOT NULL,
    data_category         VARCHAR(100) NOT NULL,
    transfer_mechanism    VARCHAR(100) NOT NULL,
    status                VARCHAR(50)  NOT NULL DEFAULT 'PENDING',
    risk_level            VARCHAR(20),
    compliance_score      INTEGER      CHECK (compliance_score BETWEEN 0 AND 100),
    ai_description        TEXT,
    ai_recommendations    TEXT,
    ai_report             TEXT,
    deadline_date         DATE,
    created_by            BIGINT       REFERENCES users(id),
    deleted               BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at            TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at            TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_transfers_status      ON data_transfers(status);
CREATE INDEX idx_transfers_source      ON data_transfers(source_country);
CREATE INDEX idx_transfers_destination ON data_transfers(destination_country);
CREATE INDEX idx_transfers_created_by  ON data_transfers(created_by);
CREATE INDEX idx_transfers_deleted     ON data_transfers(deleted);
CREATE INDEX idx_transfers_deadline    ON data_transfers(deadline_date);
