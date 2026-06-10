-- 기존 직원 마스터 담당자와 이메일이 일치하는 활성 사용자가 있으면 영업 사이트 담당자를 사용자 기준으로 연결한다.
UPDATE opportunities o
SET owner_user_id = u.user_id
FROM employees e
INNER JOIN users u
    ON LOWER(u.email) = LOWER(e.email)
WHERE o.owner_employee_id = e.employee_id
  AND o.owner_user_id IS NULL
  AND u.is_active = TRUE;
