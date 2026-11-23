CREATE TABLE IF NOT EXISTS schema_baseline (
    id SMALLINT PRIMARY KEY,
    note TEXT NOT NULL
);

INSERT INTO schema_baseline (id, note)
VALUES (1, 'cafe-ops baseline')
ON CONFLICT (id) DO NOTHING;