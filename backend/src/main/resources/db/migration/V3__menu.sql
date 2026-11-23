CREATE TABLE menu_category (
    id              BIGSERIAL PRIMARY KEY,
    cafe_id         BIGINT NOT NULL REFERENCES cafe (id),
    name            VARCHAR(80) NOT NULL,
    sort_order      INT NOT NULL DEFAULT 0,
    active          BOOLEAN NOT NULL DEFAULT true
);

CREATE TABLE menu_item (
    id              BIGSERIAL PRIMARY KEY,
    category_id     BIGINT NOT NULL REFERENCES menu_category (id),
    name            VARCHAR(120) NOT NULL,
    price           NUMERIC(10, 2) NOT NULL,
    active          BOOLEAN NOT NULL DEFAULT true,
    CONSTRAINT ck_menu_item_price CHECK (price >= 0)
);

CREATE INDEX idx_menu_category_cafe_id ON menu_category (cafe_id);
CREATE INDEX idx_menu_item_category_id ON menu_item (category_id);