CREATE TABLE business_years (
    business_year INTEGER PRIMARY KEY CHECK (business_year BETWEEN 2000 AND 2100),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

INSERT INTO business_years (business_year)
SELECT DISTINCT EXTRACT(YEAR FROM o.created_at)::INTEGER
FROM opportunities o
UNION
SELECT EXTRACT(YEAR FROM CURRENT_DATE)::INTEGER
ON CONFLICT (business_year) DO NOTHING;

CREATE TABLE customers (
    customer_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL UNIQUE,
    contact_name VARCHAR(100),
    phone VARCHAR(50),
    email VARCHAR(255),
    memo VARCHAR(1000),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_by BIGINT REFERENCES users(user_id),
    updated_by BIGINT REFERENCES users(user_id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

INSERT INTO customers (name, is_active)
SELECT DISTINCT TRIM(o.customer_name), TRUE
FROM opportunities o
WHERE TRIM(COALESCE(o.customer_name, '')) <> ''
ON CONFLICT (name) DO NOTHING;

ALTER TABLE opportunities
ADD COLUMN sales_year INTEGER,
ADD COLUMN customer_id BIGINT REFERENCES customers(customer_id),
ADD COLUMN expected_order_year INTEGER,
ADD COLUMN expected_order_quarter INTEGER CHECK (expected_order_quarter BETWEEN 1 AND 4),
ADD COLUMN expected_delivery_year INTEGER,
ADD COLUMN expected_delivery_quarter INTEGER CHECK (expected_delivery_quarter BETWEEN 1 AND 4);

UPDATE opportunities o
SET
    sales_year = EXTRACT(YEAR FROM o.created_at)::INTEGER,
    customer_id = c.customer_id,
    expected_order_year = CASE
        WHEN o.expected_order_period ~ '[0-9]{4}' THEN SUBSTRING(o.expected_order_period FROM '([0-9]{4})')::INTEGER
        ELSE NULL
    END,
    expected_order_quarter = CASE
        WHEN UPPER(o.expected_order_period) ~ '[1-4][ ]*Q' THEN SUBSTRING(UPPER(o.expected_order_period) FROM '([1-4])[ ]*Q')::INTEGER
        ELSE NULL
    END,
    expected_delivery_year = CASE
        WHEN o.expected_delivery_period ~ '[0-9]{4}' THEN SUBSTRING(o.expected_delivery_period FROM '([0-9]{4})')::INTEGER
        ELSE NULL
    END,
    expected_delivery_quarter = CASE
        WHEN UPPER(o.expected_delivery_period) ~ '[1-4][ ]*Q' THEN SUBSTRING(UPPER(o.expected_delivery_period) FROM '([1-4])[ ]*Q')::INTEGER
        ELSE NULL
    END
FROM customers c
WHERE c.name = TRIM(o.customer_name);

UPDATE opportunities
SET sales_year = EXTRACT(YEAR FROM CURRENT_DATE)::INTEGER
WHERE sales_year IS NULL;

ALTER TABLE opportunities
ALTER COLUMN sales_year SET NOT NULL;

ALTER TABLE opportunities
ALTER COLUMN sales_year SET DEFAULT (EXTRACT(YEAR FROM CURRENT_DATE)::INTEGER);

ALTER TABLE opportunities
ADD CONSTRAINT fk_opportunities_sales_year
FOREIGN KEY (sales_year) REFERENCES business_years(business_year);

CREATE TABLE annual_revenue_targets (
    annual_revenue_target_id BIGSERIAL PRIMARY KEY,
    business_year INTEGER NOT NULL REFERENCES business_years(business_year),
    department_id BIGINT NOT NULL REFERENCES departments(department_id),
    target_amount NUMERIC(15, 2) NOT NULL DEFAULT 0 CHECK (target_amount >= 0),
    created_by BIGINT REFERENCES users(user_id),
    updated_by BIGINT REFERENCES users(user_id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (business_year, department_id)
);

CREATE INDEX idx_business_years_is_active ON business_years(is_active);
CREATE INDEX idx_customers_is_active ON customers(is_active);
CREATE INDEX idx_opportunities_sales_year ON opportunities(sales_year);
CREATE INDEX idx_opportunities_customer_id ON opportunities(customer_id);
CREATE INDEX idx_opportunities_expected_order ON opportunities(expected_order_year, expected_order_quarter);
CREATE INDEX idx_opportunities_expected_delivery ON opportunities(expected_delivery_year, expected_delivery_quarter);
CREATE INDEX idx_annual_revenue_targets_year_department
    ON annual_revenue_targets(business_year, department_id);
