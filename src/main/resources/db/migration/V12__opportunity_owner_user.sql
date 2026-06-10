ALTER TABLE opportunities
ADD COLUMN owner_user_id BIGINT REFERENCES users(user_id);

CREATE INDEX idx_opportunities_owner_user_id ON opportunities(owner_user_id);
