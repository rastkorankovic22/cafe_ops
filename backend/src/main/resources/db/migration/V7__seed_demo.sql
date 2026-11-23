INSERT INTO cafe (id, name)
VALUES (1, 'Lumen Cafe')
ON CONFLICT DO NOTHING;

SELECT setval(pg_get_serial_sequence('cafe', 'id'), (SELECT COALESCE(MAX(id), 1) FROM cafe));

INSERT INTO app_user (cafe_id, email, password_hash, role, full_name, enabled)
VALUES
    (1, 'owner@lumen.cafe', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQcBy6YZdKFaBhBGY0/luAi2SOGu', 'OWNER', 'Demo Owner', true),
    (1, 'waiter1@lumen.cafe', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQcBy6YZdKFaBhBGY0/luAi2SOGu', 'WAITER', 'Ana Waiter', true),
    (1, 'waiter2@lumen.cafe', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQcBy6YZdKFaBhBGY0/luAi2SOGu', 'WAITER', 'Marko Waiter', true)
ON CONFLICT DO NOTHING;

INSERT INTO menu_category (cafe_id, name, sort_order)
VALUES
    (1, 'Coffee', 1),
    (1, 'Tea', 2),
    (1, 'Juice', 3),
    (1, 'Other', 4);

INSERT INTO menu_item (category_id, name, price)
SELECT c.id, v.name, v.price
FROM menu_category c
CROSS JOIN (VALUES
    ('Espresso', 180.00),
    ('Americano', 200.00),
    ('Cappuccino', 260.00),
    ('Latte', 280.00),
    ('Mocha', 300.00)
) AS v(name, price)
WHERE c.name = 'Coffee' AND c.cafe_id = 1;

INSERT INTO menu_item (category_id, name, price)
SELECT c.id, v.name, v.price
FROM menu_category c
CROSS JOIN (VALUES
    ('Green Tea', 220.00),
    ('Black Tea', 200.00),
    ('Chamomile', 220.00)
) AS v(name, price)
WHERE c.name = 'Tea' AND c.cafe_id = 1;

INSERT INTO menu_item (category_id, name, price)
SELECT c.id, v.name, v.price
FROM menu_category c
CROSS JOIN (VALUES
    ('Orange Juice', 320.00),
    ('Lemonade', 300.00)
) AS v(name, price)
WHERE c.name = 'Juice' AND c.cafe_id = 1;

INSERT INTO menu_item (category_id, name, price)
SELECT c.id, v.name, v.price
FROM menu_category c
CROSS JOIN (VALUES
    ('Sparkling Water', 150.00),
    ('Still Water', 120.00)
) AS v(name, price)
WHERE c.name = 'Other' AND c.cafe_id = 1;

INSERT INTO cafe_table (cafe_id, label, pos_x, pos_y)
VALUES
    (1, 'T1', 80, 80),
    (1, 'T2', 200, 80),
    (1, 'T3', 320, 80),
    (1, 'T4', 80, 200),
    (1, 'T5', 200, 200),
    (1, 'T6', 320, 200),
    (1, 'T7', 80, 320),
    (1, 'T8', 200, 320);