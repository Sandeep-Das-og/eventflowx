CREATE TABLE IF NOT EXISTS wallets (
    user_id VARCHAR(255) PRIMARY KEY,
    balance DOUBLE PRECISION NOT NULL,
    version BIGINT
);

CREATE TABLE IF NOT EXISTS processed_events (
    event_id VARCHAR(255) PRIMARY KEY,
    processed_at TIMESTAMP NOT NULL
);
