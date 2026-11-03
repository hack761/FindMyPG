CREATE TABLE audit_events (id BIGSERIAL PRIMARY KEY, actor_id BIGINT NOT NULL REFERENCES accounts(id), action VARCHAR(80) NOT NULL, entity_type VARCHAR(80) NOT NULL, entity_id BIGINT NOT NULL, detail VARCHAR(500), created_at TIMESTAMPTZ NOT NULL DEFAULT now());
CREATE INDEX audit_events_created_idx ON audit_events(created_at DESC);
