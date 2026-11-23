CREATE TABLE cafe_table (
    id              BIGSERIAL PRIMARY KEY,
    cafe_id         BIGINT NOT NULL REFERENCES cafe (id),
    label           VARCHAR(20) NOT NULL,
    pos_x           INT NOT NULL DEFAULT 0,
    pos_y           INT NOT NULL DEFAULT 0,
    active          BOOLEAN NOT NULL DEFAULT true,
    CONSTRAINT uq_cafe_table_label_per_cafe UNIQUE (cafe_id, label)
);

CREATE INDEX idx_cafe_table_cafe_id ON cafe_table (cafe_id);