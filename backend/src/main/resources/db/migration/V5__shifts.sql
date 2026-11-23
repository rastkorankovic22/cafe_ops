CREATE TABLE shift (
    id              BIGSERIAL PRIMARY KEY,
    cafe_id         BIGINT NOT NULL REFERENCES cafe (id),
    waiter_id       BIGINT NOT NULL REFERENCES app_user (id),
    status          VARCHAR(20) NOT NULL,
    started_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    ended_at        TIMESTAMPTZ,
    CONSTRAINT ck_shift_status CHECK (status IN ('OPEN', 'CLOSED')),
    CONSTRAINT ck_shift_ended_after_start CHECK (ended_at IS NULL OR ended_at >= started_at)
);

CREATE INDEX idx_shift_cafe_id ON shift (cafe_id);
CREATE INDEX idx_shift_waiter_id ON shift (waiter_id);

CREATE UNIQUE INDEX uq_shift_one_open_per_cafe
    ON shift (cafe_id)
    WHERE status = 'OPEN';