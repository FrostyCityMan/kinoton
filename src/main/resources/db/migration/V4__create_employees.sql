CREATE TABLE employees (
    employee_id BIGSERIAL PRIMARY KEY,
    department_id BIGINT REFERENCES departments(department_id),
    name VARCHAR(100) NOT NULL,
    position_name VARCHAR(100),
    email VARCHAR(255) UNIQUE,
    phone VARCHAR(50),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

ALTER TABLE opportunities
ADD COLUMN owner_employee_id BIGINT REFERENCES employees(employee_id);

CREATE INDEX idx_employees_department_id ON employees(department_id);
CREATE INDEX idx_employees_is_active ON employees(is_active);
CREATE INDEX idx_opportunities_owner_employee_id ON opportunities(owner_employee_id);
