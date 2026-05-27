CREATE TABLE opportunity_executive_comments (
    opportunity_executive_comment_id BIGSERIAL PRIMARY KEY,
    opportunity_id BIGINT NOT NULL REFERENCES opportunities(opportunity_id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    created_by BIGINT REFERENCES users(user_id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_opportunity_executive_comments_opportunity_id
    ON opportunity_executive_comments(opportunity_id);

CREATE INDEX idx_opportunity_executive_comments_created_by
    ON opportunity_executive_comments(created_by);
