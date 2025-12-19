-- Insert users (table name is 'users', not 'appuser')
INSERT INTO users (id, username, password, name, email, phone, balance, role, active, created_at, updated_at) VALUES (1, 'admin', '$2a$10$BGDNy/UW5ORUfHGZlTEw.ew0d4lZAUgGwEMUg75db2vxtApznQzYa', 'The Admin', 'mm@gmail.com', '67 123 123', 0.0, 'ADMIN', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO users (id, username, password, name, email, phone, balance, role, active, created_at, updated_at) VALUES (2, 'john', '$2a$10$BGDNy/UW5ORUfHGZlTEw.ew0d4lZAUgGwEMUg75db2vxtApznQzYa', 'John Doe', 'pp@gmail.com', '67 100 100', 0.0, 'USER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO users (id, username, password, name, email, phone, balance, role, active, created_at, updated_at) VALUES (3, 'rich', '$2a$10$BGDNy/UW5ORUfHGZlTEw.ew0d4lZAUgGwEMUg75db2vxtApznQzYa', 'Guy Rich', 'jj@gmail.com', '67 111 222', 100000.0, 'USER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert item types
INSERT INTO item_types (id, name, description, image_path, created_at, updated_at) VALUES (1, 'Cars&Vehicles', 'Cars and other transportation. Scooters, bikes, trains, boats, yachts, airships..', 'Categories/1.jpg', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO item_types (id, name, description, image_path, created_at, updated_at) VALUES (2, 'Home&Kitchen', 'Everything for your home, small home and kitchen electronics.', 'Categories/2.jpg', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO item_types (id, name, description, image_path, created_at, updated_at) VALUES (3, 'Electronics', 'All kinds of devices. Gaming equipment, laptops, home appliances etc.', 'Categories/3.jpg', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO item_types (id, name, description, image_path, created_at, updated_at) VALUES (4, 'Sports&Outdoors', 'Sports clothing and sport requisits.', 'Categories/4.jpg', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO item_types (id, name, description, image_path, created_at, updated_at) VALUES (5, 'Womens clothing', 'Everything for women.', 'Categories/5.jpg', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO item_types (id, name, description, image_path, created_at, updated_at) VALUES (6, 'Jewelry&Accesories', 'All kinds of jewelry and decorative items.', 'Categories/6.jpg', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO item_types (id, name, description, image_path, created_at, updated_at) VALUES (7, 'Mens clothing', 'Clothing items for men', 'Categories/7.jpg', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO item_types (id, name, description, image_path, created_at, updated_at) VALUES (8, 'Books&Media', 'Books and stuff.', 'Categories/8.jpg', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO item_types (id, name, description, image_path, created_at, updated_at) VALUES (9, 'Services', 'For various intelectual or physical services.', 'Categories/9.jpg', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO item_types (id, name, description, image_path, created_at, updated_at) VALUES (10, 'Other', 'Whatever else.', 'Categories/10.jpg', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert items (updated_at added)
INSERT INTO items (id, name, description, price, item_type_id, seller_id, active, deleted, created_at, updated_at) VALUES (1, 'Audi A4', 'Red color, 2021., 35 TFSI', 30000, 1, 2, true, false, '2025-10-19 17:43:38.8328', CURRENT_TIMESTAMP);
INSERT INTO items (id, name, description, price, item_type_id, seller_id, active, deleted, created_at, updated_at) VALUES (2, 'Audi A5', 'Pure luxury, 2021., 40 TFSI', 40000, 1, 2, true, false, '2025-10-19 17:43:38.8328', CURRENT_TIMESTAMP);
INSERT INTO items (id, name, description, price, item_type_id, seller_id, active, deleted, created_at, updated_at) VALUES (3, 'Audi Q5', 'SUV version of classic Audi, 2021., 45 TDI', 65000, 1, 2, true, false, '2025-10-19 17:43:38.8328', CURRENT_TIMESTAMP);
INSERT INTO items (id, name, description, price, item_type_id, seller_id, active, deleted, created_at, updated_at) VALUES (4, 'Audi TT', 'Old school 2016., 3.0 engine', 15000, 1, 2, true, false, '2025-10-19 17:43:38.8328', CURRENT_TIMESTAMP);
INSERT INTO items (id, name, description, price, item_type_id, seller_id, active, deleted, created_at, updated_at) VALUES (5, 'Lotus elise', 'Cult classic from 2014., 4.0 gasoline', 60000, 1, 2, true, false, '2025-10-19 17:43:38.8328', CURRENT_TIMESTAMP);

-- Insert images (created_at added)
INSERT INTO images (id, path, item_id, front, created_at) VALUES (1, 'audi_a4.jpg', 1, true, CURRENT_TIMESTAMP);
INSERT INTO images (id, path, item_id, front, created_at) VALUES (2, 'audi_a4_2.jpg', 1, false, CURRENT_TIMESTAMP);
INSERT INTO images (id, path, item_id, front, created_at) VALUES (3, 'audi_a4_3.jpg', 1, false, CURRENT_TIMESTAMP);
INSERT INTO images (id, path, item_id, front, created_at) VALUES (4, 'audi_a5.jpg', 2, true, CURRENT_TIMESTAMP);
INSERT INTO images (id, path, item_id, front, created_at) VALUES (5, 'audi_q5.jpg', 3, true, CURRENT_TIMESTAMP);
INSERT INTO images (id, path, item_id, front, created_at) VALUES (6, 'audi_tt.jpg', 4, true, CURRENT_TIMESTAMP);
INSERT INTO images (id, path, item_id, front, created_at) VALUES (7, 'lotuselise.jpg', 5, true, CURRENT_TIMESTAMP);

