CREATE TABLE data_transfer (
    id BIGSERIAL PRIMARY KEY,
    source_country VARCHAR(255) NOT NULL,
    destination_country VARCHAR(255) NOT NULL,
    data_type VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    description TEXT,
    compliance_score INTEGER,
    risk_level VARCHAR(50),
    legal_basis VARCHAR(255),
    created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_data_transfer_status ON data_transfer(status);
CREATE INDEX idx_data_transfer_source_country ON data_transfer(source_country);
CREATE INDEX idx_data_transfer_destination_country ON data_transfer(destination_country);
CREATE INDEX idx_data_transfer_compliance_score ON data_transfer(compliance_score);
CREATE INDEX idx_data_transfer_risk_level ON data_transfer(risk_level);

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    role VARCHAR(50) NOT NULL
);