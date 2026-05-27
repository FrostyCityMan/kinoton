ALTER TABLE opportunities
ADD COLUMN security_level VARCHAR(30) NOT NULL DEFAULT 'GENERAL'
    CHECK (security_level IN ('GENERAL', 'CONFIDENTIAL'));

CREATE TABLE opportunity_view_permissions (
    opportunity_id BIGINT NOT NULL REFERENCES opportunities(opportunity_id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    created_by BIGINT REFERENCES users(user_id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (opportunity_id, user_id)
);

CREATE INDEX idx_opportunities_security_level ON opportunities(security_level);
CREATE INDEX idx_opportunity_view_permissions_user_id ON opportunity_view_permissions(user_id);

INSERT INTO opportunity_view_permissions (
    opportunity_id,
    user_id,
    created_by
)
SELECT
    o.opportunity_id,
    o.created_by,
    o.created_by
FROM opportunities o
WHERE o.created_by IS NOT NULL
ON CONFLICT (opportunity_id, user_id) DO NOTHING;
