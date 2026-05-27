-- Local seed data for development profile. Keep this file encoded as UTF-8.
INSERT INTO departments (code, name, display_order)
VALUES
    ('DE', 'DE 사업본부', 1),
    ('DX', 'Dx 사업본부', 2),
    ('STRATEGY', '미래전략본부', 3)
ON CONFLICT (code) DO UPDATE
SET
    name = EXCLUDED.name,
    display_order = EXCLUDED.display_order,
    updated_at = NOW();

INSERT INTO roles (code, name, description)
VALUES
    ('ADMIN', '관리자', '전체 기능, 사용자 관리, 설정 변경 권한'),
    ('EXECUTIVE', '임원 모니터링', '전체 본부 대시보드 조회 및 합의된 추가 입력 권한'),
    ('DEPARTMENT_USER', '본부 담당자', '본인 본부 데이터 조회 및 입력 권한')
ON CONFLICT (code) DO UPDATE
SET
    name = EXCLUDED.name,
    description = EXCLUDED.description;

INSERT INTO probability_stages (department_id, probability, name, description, is_confirmed_revenue, display_order)
SELECT d.department_id, stage.probability, stage.name, stage.description, stage.is_confirmed_revenue, stage.display_order
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
    is_active = TRUE,
    updated_at = NOW();

INSERT INTO users (email, password_hash, name, is_active, is_password_reset_required)
VALUES
    ('admin@kinoton.local', '{bcrypt}$2a$10$8BEoCco7dw821f7kKbOaeOBWE1rHCHZ0EVaCcSDQTzhct2jqF62QK', '로컬 관리자', TRUE, FALSE),
    ('executive@kinoton.local', '{bcrypt}$2a$10$8BEoCco7dw821f7kKbOaeOBWE1rHCHZ0EVaCcSDQTzhct2jqF62QK', '로컬 임원', TRUE, FALSE),
    ('de.user@kinoton.local', '{bcrypt}$2a$10$8BEoCco7dw821f7kKbOaeOBWE1rHCHZ0EVaCcSDQTzhct2jqF62QK', 'DE 담당자', TRUE, FALSE)
ON CONFLICT (email) DO UPDATE
SET
    password_hash = EXCLUDED.password_hash,
    name = EXCLUDED.name,
    is_active = EXCLUDED.is_active,
    is_password_reset_required = EXCLUDED.is_password_reset_required,
    updated_at = NOW();

DELETE FROM user_roles ur
USING users u
WHERE u.user_id = ur.user_id
  AND u.email IN ('admin@kinoton.local', 'executive@kinoton.local', 'de.user@kinoton.local');

INSERT INTO user_roles (user_id, role_id)
SELECT u.user_id, r.role_id
FROM (
    VALUES
        ('admin@kinoton.local', 'ADMIN'),
        ('executive@kinoton.local', 'EXECUTIVE'),
        ('de.user@kinoton.local', 'DEPARTMENT_USER')
) AS assignment(email, role_code)
INNER JOIN users u
    ON u.email = assignment.email
INNER JOIN roles r
    ON r.code = assignment.role_code
ON CONFLICT (user_id, role_id) DO NOTHING;

DELETE FROM user_department_permissions udp
USING users u
WHERE u.user_id = udp.user_id
  AND u.email = 'de.user@kinoton.local';

INSERT INTO user_department_permissions (user_id, department_id, can_read, can_write)
SELECT u.user_id, d.department_id, TRUE, TRUE
FROM users u
INNER JOIN departments d
    ON d.code = 'DE'
WHERE u.email = 'de.user@kinoton.local'
ON CONFLICT (user_id, department_id) DO UPDATE
SET
    can_read = EXCLUDED.can_read,
    can_write = EXCLUDED.can_write;
