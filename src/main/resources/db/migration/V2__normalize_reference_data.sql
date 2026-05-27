-- 기본 사업본부 기준 데이터를 보정한다.
INSERT INTO departments (code, name, display_order, is_active)
VALUES
    ('DE', 'DE 사업본부', 1, TRUE),
    ('DX', 'Dx 사업본부', 2, TRUE),
    ('STRATEGY', '미래전략본부', 3, TRUE)
ON CONFLICT (code) DO UPDATE
SET
    name = EXCLUDED.name,
    display_order = EXCLUDED.display_order,
    is_active = EXCLUDED.is_active,
    updated_at = NOW();

-- 기본 수주확률 단계 표시값을 보정한다.
INSERT INTO probability_stages (department_id, probability, name, description, is_confirmed_revenue, display_order, is_active)
SELECT
    d.department_id,
    stage.probability,
    stage.name,
    stage.description,
    stage.is_confirmed_revenue,
    stage.display_order,
    TRUE
FROM departments d
CROSS JOIN (
    VALUES
        (10, '영업 시작', '영업 활동 개시 및 고객 니즈 파악 단계', FALSE, 1),
        (30, '제안서 제출', '제안서 제출 및 기본 조건 협의 단계', FALSE, 2),
        (60, '시방서 반영', '시방서 또는 주요 요구사항 반영 단계', FALSE, 3),
        (90, '계약서 초안', '계약서 초안 협의 및 확정매출 집계 단계', TRUE, 4),
        (95, '계약 확정', '계약 체결 완료 및 매출 확정 단계', TRUE, 5)
) AS stage(probability, name, description, is_confirmed_revenue, display_order)
ON CONFLICT (department_id, probability) DO UPDATE
SET
    name = EXCLUDED.name,
    description = EXCLUDED.description,
    is_confirmed_revenue = EXCLUDED.is_confirmed_revenue,
    display_order = EXCLUDED.display_order,
    is_active = EXCLUDED.is_active,
    updated_at = NOW();
