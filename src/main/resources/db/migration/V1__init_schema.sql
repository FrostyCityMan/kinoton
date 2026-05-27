CREATE TABLE departments (
    department_id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    display_order INTEGER NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE roles (
    role_id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE users (
    user_id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_password_reset_required BOOLEAN NOT NULL DEFAULT TRUE,
    last_login_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL REFERENCES users(user_id),
    role_id BIGINT NOT NULL REFERENCES roles(role_id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE user_department_permissions (
    user_id BIGINT NOT NULL REFERENCES users(user_id),
    department_id BIGINT NOT NULL REFERENCES departments(department_id),
    can_read BOOLEAN NOT NULL DEFAULT TRUE,
    can_write BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (user_id, department_id)
);

CREATE TABLE probability_stages (
    probability_stage_id BIGSERIAL PRIMARY KEY,
    department_id BIGINT NOT NULL REFERENCES departments(department_id),
    probability INTEGER NOT NULL CHECK (probability BETWEEN 0 AND 100),
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    is_confirmed_revenue BOOLEAN NOT NULL DEFAULT FALSE,
    display_order INTEGER NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (department_id, probability)
);

CREATE TABLE opportunities (
    opportunity_id BIGSERIAL PRIMARY KEY,
    department_id BIGINT NOT NULL REFERENCES departments(department_id),
    customer_name VARCHAR(200) NOT NULL,
    project_name VARCHAR(200) NOT NULL,
    owner_name VARCHAR(100) NOT NULL,
    expected_order_period VARCHAR(50),
    expected_delivery_period VARCHAR(50),
    project_amount NUMERIC(15, 2) NOT NULL DEFAULT 0,
    probability_stage_id BIGINT NOT NULL REFERENCES probability_stages(probability_stage_id),
    status VARCHAR(30) NOT NULL DEFAULT 'IN_PROGRESS'
        CHECK (status IN ('IN_PROGRESS', 'WON', 'HOLD', 'LOST')),
    created_by BIGINT REFERENCES users(user_id),
    updated_by BIGINT REFERENCES users(user_id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE opportunity_progress (
    opportunity_progress_id BIGSERIAL PRIMARY KEY,
    opportunity_id BIGINT NOT NULL REFERENCES opportunities(opportunity_id) ON DELETE CASCADE,
    progress_date DATE NOT NULL,
    probability_stage_id BIGINT NOT NULL REFERENCES probability_stages(probability_stage_id),
    content TEXT NOT NULL,
    created_by BIGINT REFERENCES users(user_id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE attachments (
    attachment_id BIGSERIAL PRIMARY KEY,
    opportunity_id BIGINT NOT NULL REFERENCES opportunities(opportunity_id) ON DELETE CASCADE,
    opportunity_progress_id BIGINT REFERENCES opportunity_progress(opportunity_progress_id) ON DELETE SET NULL,
    original_filename VARCHAR(255) NOT NULL,
    stored_filename VARCHAR(255) NOT NULL,
    content_type VARCHAR(100),
    file_size_bytes BIGINT NOT NULL CHECK (file_size_bytes >= 0),
    storage_path VARCHAR(1000) NOT NULL,
    uploaded_by BIGINT REFERENCES users(user_id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE audit_logs (
    audit_log_id BIGSERIAL PRIMARY KEY,
    actor_user_id BIGINT REFERENCES users(user_id),
    target_type VARCHAR(100) NOT NULL,
    target_id BIGINT,
    action VARCHAR(100) NOT NULL,
    before_data JSONB,
    after_data JSONB,
    ip_address VARCHAR(100),
    user_agent VARCHAR(500),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_opportunities_department_id ON opportunities(department_id);
CREATE INDEX idx_opportunities_status ON opportunities(status);
CREATE INDEX idx_opportunities_probability_stage_id ON opportunities(probability_stage_id);
CREATE INDEX idx_opportunity_progress_opportunity_id ON opportunity_progress(opportunity_id);
CREATE INDEX idx_attachments_opportunity_id ON attachments(opportunity_id);
CREATE INDEX idx_audit_logs_target ON audit_logs(target_type, target_id);
CREATE INDEX idx_audit_logs_actor_user_id ON audit_logs(actor_user_id);
