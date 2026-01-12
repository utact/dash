-- Fix foreign key references from 'user' to 'users' table

-- Drop existing foreign keys
ALTER TABLE battle DROP FOREIGN KEY battle_ibfk_1;
ALTER TABLE battle_participant DROP FOREIGN KEY battle_participant_ibfk_2;

-- Re-create foreign keys with correct table name 'users'
ALTER TABLE battle ADD CONSTRAINT fk_battle_creator FOREIGN KEY (creator_id) REFERENCES users(id);
ALTER TABLE battle_participant ADD CONSTRAINT fk_battle_participant_user FOREIGN KEY (user_id) REFERENCES users(id);
