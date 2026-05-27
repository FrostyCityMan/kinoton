-- 기존 사업본부별 수주확률 단계를 확률 기준 전사 공통 단계로 통합한다.
WITH ranked_stages AS (
    SELECT
        ps.probability_stage_id,
        ps.probability,
        ROW_NUMBER() OVER (
            PARTITION BY ps.probability
            ORDER BY ps.updated_at DESC, ps.probability_stage_id DESC
        ) AS row_number
    FROM probability_stages ps
),
canonical_stages AS (
    SELECT
        rs.probability,
        rs.probability_stage_id AS canonical_probability_stage_id
    FROM ranked_stages rs
    WHERE rs.row_number = 1
)
UPDATE opportunities o
SET probability_stage_id = cs.canonical_probability_stage_id
FROM probability_stages ps
INNER JOIN canonical_stages cs
    ON cs.probability = ps.probability
WHERE o.probability_stage_id = ps.probability_stage_id
  AND o.probability_stage_id <> cs.canonical_probability_stage_id;

-- 일자별 진행 기록의 수주확률 참조도 전사 공통 기준으로 통합한다.
WITH ranked_stages AS (
    SELECT
        ps.probability_stage_id,
        ps.probability,
        ROW_NUMBER() OVER (
            PARTITION BY ps.probability
            ORDER BY ps.updated_at DESC, ps.probability_stage_id DESC
        ) AS row_number
    FROM probability_stages ps
),
canonical_stages AS (
    SELECT
        rs.probability,
        rs.probability_stage_id AS canonical_probability_stage_id
    FROM ranked_stages rs
    WHERE rs.row_number = 1
)
UPDATE opportunity_progress op
SET probability_stage_id = cs.canonical_probability_stage_id
FROM probability_stages ps
INNER JOIN canonical_stages cs
    ON cs.probability = ps.probability
WHERE op.probability_stage_id = ps.probability_stage_id
  AND op.probability_stage_id <> cs.canonical_probability_stage_id;

-- 중복 수주확률 단계는 참조 정리 후 제거한다.
WITH ranked_stages AS (
    SELECT
        ps.probability_stage_id,
        ROW_NUMBER() OVER (
            PARTITION BY ps.probability
            ORDER BY ps.updated_at DESC, ps.probability_stage_id DESC
        ) AS row_number
    FROM probability_stages ps
)
DELETE FROM probability_stages ps
USING ranked_stages rs
WHERE ps.probability_stage_id = rs.probability_stage_id
  AND rs.row_number > 1;

-- 수주확률 단계는 더 이상 사업본부에 종속되지 않는다.
ALTER TABLE probability_stages
DROP CONSTRAINT IF EXISTS probability_stages_department_id_probability_key;

ALTER TABLE probability_stages
DROP COLUMN department_id;

ALTER TABLE probability_stages
ADD CONSTRAINT probability_stages_probability_key UNIQUE (probability);
