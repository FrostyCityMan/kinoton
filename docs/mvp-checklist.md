# Kinoton MVP 구축 체크리스트

작성일: 2026-05-26

## 1. 현재 완료 상태

- [x] 프로젝트 루트 구조 생성
- [x] Gradle 기반 Spring Boot 설정 초안 작성
- [x] Java 21 toolchain 기준 선언
- [x] Spring Security 기본 설정 추가
- [x] MyBatis Mapper Scan 설정 추가
- [x] ApiResponse wrapper 생성
- [x] GlobalExceptionHandler 생성
- [x] Dashboard Controller / Service / ServiceImpl / DAO / Mapper XML 흐름 생성
- [x] `/api/v1/dashboard/summary` API 초안 생성
- [x] Thymeleaf 대시보드 화면 초안 생성
- [x] 계약 범위 기반 사이드바 메뉴 초안 생성
- [x] Flyway V1 초기 스키마 초안 생성
- [x] 운영 secret 환경변수 외부화 기준 작성

## 2. 즉시 해결해야 하는 환경 Gate

- [x] JDK 21 Gradle toolchain provisioning 설정
- [x] Gradle Wrapper 생성
- [x] PostgreSQL 로컬 DB 생성
- [x] `kinoton` DB 사용자 및 권한 생성
- [x] `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `STORAGE_ROOT` 환경변수 확정
- [x] `./gradlew.bat test` 실행
- [x] Flyway V1/local seed DB 적용
- [x] `./gradlew.bat bootRun` 실행
- [x] `/dashboard` 접속 확인

## 3. DB-first 설계 Gate

- [x] 부서 코드 확정: `DE`, `DX`, `STRATEGY`
- [x] 역할 코드 확정: `ADMIN`, `EXECUTIVE`, `DEPARTMENT_USER`
- [x] 영업 상태 코드 확정: `IN_PROGRESS`, `WON`, `HOLD`, `LOST`
- [x] 수주확률 단계 기본값 확정: 10, 30, 60, 90
- [x] 확정매출 집계 기준 확정: 수주확률 90 이상
- [x] 기대매출 집계 기준 확정: 수주확률 90 미만
- [x] 본부 담당자 권한 범위 확정: `user_department_permissions` 기준 본인 본부 조회·입력
- [x] 임원 권한 범위 확정: 전체 본부 조회·입력, 클라이언트 확인 시 조회 전용으로 조정 가능
- [x] 첨부파일 제한 확정: MVP 기본값 20MB, 문서/이미지/텍스트 확장자, 보관 기간은 영업 사이트 생명주기 기준
- [x] 보고서 양식 확정: 영업 사이트 기준 화면 출력, Excel 호환 XLS, PDF 다운로드

## 4. Backend 구현 체크리스트

- [x] Flyway local seed 기준 작성
- [x] 운영 migration과 local seed 분리
- [x] 사용자/역할/부서 Mapper 작성
- [x] Spring Security DB 인증 연동
- [x] 로그인/로그아웃 화면 구현
- [x] 회원가입 신청 API 구현
- [x] 비밀번호 암호화 정책 적용
- [x] 권한별 접근 제어 적용
- [x] 대시보드 본부별 집계 API 구현
- [x] 영업 사이트 등록 API 구현
- [x] 영업 사이트 목록 API 구현
- [x] 영업 진행 상세 API 구현
- [x] 영업 진행 기록 추가 API 구현
- [x] 수주확률 단계 조회/저장 API 구현
- [x] 회원 관리 API 구현
- [x] 권한 관리 API 구현
- [x] 첨부파일 업로드/다운로드/삭제 API 구현
- [x] 보고서 Excel 다운로드 구현
- [x] 보고서 PDF 생성 구현
- [x] 인쇄용 화면 기본 구현
- [x] 감사 로그 기록 적용
- [x] GlobalExceptionHandler 업무 예외 세분화

## 5. Frontend 구현 체크리스트

- [ ] 공통 layout fragment 확정
- [ ] 공통 sidebar fragment 확정
- [x] 로그인 화면 구현
- [x] 회원가입 화면 구현
- [x] 대시보드 화면 구현
- [ ] 신규 영업 사이트 등록 화면 또는 모달 구현
- [x] 본부별 영업 사이트 리스트 화면 구현
- [x] 영업 진행 상세 화면 구현
- [x] 영업 진행 기록 입력 폼 구현
- [x] 수주확률 설정 화면 구현
- [x] 보고서 화면 구현
- [x] 회원 관리 화면 구현
- [x] 권한 관리 화면 구현
- [x] 첨부파일 UI 구현
- [ ] 인쇄 전용 스타일 구현
- [ ] 모바일 360px 기준 레이아웃 검증

## 6. 검수 체크리스트

- [x] 관리자 로그인 가능
- [x] 본부 담당자 로그인 가능
- [x] 임원 로그인 가능
- [x] 본부 담당자는 타 본부 데이터 접근 불가
- [x] 임원은 전체 본부 대시보드 조회 가능
- [x] 영업 사이트 등록 시 초기 진행 기록 자동 생성
- [x] 수주확률 변경 시 영업 사이트 현재 단계 반영
- [x] 수주확률 변경 시 대시보드 집계 반영
- [x] 90 이상은 확정매출로 집계
- [x] 90 미만은 기대매출로 집계
- [x] 보류/실주는 매출 집계에서 제외
- [x] 보고서 화면 출력 가능
- [x] Excel 다운로드 가능
- [x] PDF 생성 가능
- [x] 첨부파일 업로드 가능
- [x] 첨부파일 다운로드 가능
- [x] 권한 없는 사용자는 첨부파일 접근 불가
- [x] 사용자 생성/수정 가능
- [ ] 사용자 삭제 또는 비활성화 운영 정책 확정
- [x] 감사 로그 기록 확인 가능
- [ ] 운영 secret이 코드에 없음

## 7. 다음 단계 권장 순서

1. JDK 21과 Gradle Wrapper를 먼저 정리한다.
2. PostgreSQL 로컬 DB를 생성하고 Flyway V1을 실제 적용한다.
3. 부서/역할/수주확률 local seed를 추가한다.
4. Spring Security를 DB 인증으로 전환한다.
5. 로그인 후 대시보드가 실제 DB 집계로 열리게 만든다.
6. 영업 사이트 등록과 목록을 첫 CRUD slice로 구현한다.
7. 영업 진행 기록과 수주확률 집계를 연결한다.
8. 권한 분리를 서버 레벨에서 검증한다.
9. 회원 관리와 권한 관리 화면을 관리자 전용으로 구현한다.
10. 첨부파일 업로드/다운로드/삭제를 영업 상세 화면에 붙인다.
11. 감사 로그 기록 결과와 운영 secret 노출 여부를 검수한다.

## 8. 현재 Blocker

- 로컬 기본 Java가 17이어도 Gradle Java 21 toolchain provisioning으로 빌드는 가능하다.
- 현재 권한 Slice 기준의 치명적 blocker는 없다.
