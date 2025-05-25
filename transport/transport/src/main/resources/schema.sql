-- Drop existing foreign key constraints to allow cleanup
ALTER TABLE travel_card DROP FOREIGN KEY IF EXISTS FKshtkl3v6fibtulpvwhyilis7f;

-- Clean up orphaned travel_card records (cards without valid users)
DELETE FROM travel_card WHERE user_id NOT IN (SELECT id FROM users);

-- Now add the foreign key constraint
ALTER TABLE travel_card
ADD CONSTRAINT FKshtkl3v6fibtulpvwhyilis7f
FOREIGN KEY (user_id) REFERENCES users (id);
