CREATE TABLE cafe_order (
    id              BIGSERIAL PRIMARY KEY,
    table_id        BIGINT NOT NULL REFERENCES cafe_table (id),
    shift_id        BIGINT NOT NULL REFERENCES shift (id),
    status          VARCHAR(20) NOT NULL,
    opened_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    closed_at       TIMESTAMPTZ,
    CONSTRAINT ck_cafe_order_status CHECK (status IN ('OPEN', 'CLOSED', 'CANCELLED')),
    CONSTRAINT ck_cafe_order_closed_after_open CHECK (closed_at IS NULL OR closed_at >= opened_at)
);

CREATE TABLE order_line (
    id              BIGSERIAL PRIMARY KEY,
    order_id        BIGINT NOT NULL REFERENCES cafe_order (id) ON DELETE CASCADE,
    menu_item_id    BIGINT NOT NULL REFERENCES menu_item (id),
    quantity        INT NOT NULL,
    unit_price      NUMERIC(10, 2) NOT NULL,
    CONSTRAINT ck_order_line_quantity CHECK (quantity > 0),
    CONSTRAINT ck_order_line_unit_price CHECK (unit_price >= 0)
);

CREATE INDEX idx_cafe_order_table_id ON cafe_order (table_id);
CREATE INDEX idx_cafe_order_shift_id ON cafe_order (shift_id);
CREATE INDEX idx_order_line_order_id ON order_line (order_id);

CREATE UNIQUE INDEX uq_cafe_order_one_open_per_table
    ON cafe_order (table_id)
    WHERE status = 'OPEN';