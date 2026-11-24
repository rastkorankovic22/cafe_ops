ALTER TABLE menu_item
    ADD COLUMN image_url VARCHAR(500);

UPDATE menu_category SET name = 'Teas' WHERE name = 'Tea';
UPDATE menu_category SET name = 'Juices' WHERE name = 'Juice';
UPDATE menu_category SET name = 'Waters' WHERE name = 'Other';

INSERT INTO menu_category (cafe_id, name, sort_order, active)
SELECT 1, 'Alcohol', 5, true
WHERE NOT EXISTS (SELECT 1 FROM menu_category WHERE cafe_id = 1 AND name = 'Alcohol');

UPDATE menu_item SET image_url = '/drinks/coffee.svg' WHERE name IN ('Espresso', 'Americano', 'Cappuccino', 'Latte', 'Mocha');
UPDATE menu_item SET image_url = '/drinks/tea.svg' WHERE name IN ('Green Tea', 'Black Tea', 'Chamomile');
UPDATE menu_item SET image_url = '/drinks/juice.svg' WHERE name IN ('Orange Juice', 'Lemonade');
UPDATE menu_item SET image_url = '/drinks/water.svg' WHERE name IN ('Sparkling Water', 'Still Water');

INSERT INTO menu_item (category_id, name, price, image_url, active)
SELECT c.id, 'Draft Beer', 350.00, '/drinks/beer.svg', true FROM menu_category c
WHERE c.cafe_id = 1 AND c.name = 'Alcohol' AND NOT EXISTS (SELECT 1 FROM menu_item WHERE name = 'Draft Beer');

INSERT INTO menu_item (category_id, name, price, image_url, active)
SELECT c.id, 'House Wine (glass)', 420.00, '/drinks/wine.svg', true FROM menu_category c
WHERE c.cafe_id = 1 AND c.name = 'Alcohol' AND NOT EXISTS (SELECT 1 FROM menu_item WHERE name = 'House Wine (glass)');

INSERT INTO menu_item (category_id, name, price, image_url, active)
SELECT c.id, 'Rakija', 380.00, '/drinks/spirits.svg', true FROM menu_category c
WHERE c.cafe_id = 1 AND c.name = 'Alcohol' AND NOT EXISTS (SELECT 1 FROM menu_item WHERE name = 'Rakija');