ALTER TABLE users
    ADD COLUMN phone_number VARCHAR(13),
    ADD CONSTRAINT chk_users_phone_number_format
        CHECK (phone_number IS NULL OR phone_number ~ '^[0-9]{3}-[0-9]{4}-[0-9]{4}$');
