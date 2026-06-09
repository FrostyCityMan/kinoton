ALTER TABLE opportunities
ADD COLUMN expected_order_month INTEGER CHECK (expected_order_month BETWEEN 1 AND 12);

UPDATE opportunities
SET expected_order_month = CASE
    WHEN expected_order_period ~ '[0-9]{1,2}[ ]*월' THEN SUBSTRING(expected_order_period FROM '([0-9]{1,2})[ ]*월')::INTEGER
    WHEN expected_order_quarter IS NOT NULL THEN ((expected_order_quarter - 1) * 3) + 1
    ELSE NULL
END
WHERE expected_order_month IS NULL;

CREATE INDEX idx_opportunities_expected_order_month
    ON opportunities(expected_order_year, expected_order_month);
