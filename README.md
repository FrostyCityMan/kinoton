# Kinoton Sales Management System

Kinoton 영업 관리 시스템의 Spring Boot 기반 MVP입니다.

## Stack

- Java 21
- Spring Boot
- Gradle
- Spring Security
- MyBatis
- PostgreSQL
- Flyway
- Thymeleaf

## Local Environment

운영 secret은 코드에 저장하지 않습니다. 로컬 실행 시 환경변수를 설정합니다.

```powershell
$env:DB_URL='jdbc:postgresql://localhost:5432/kinoton'
$env:DB_USERNAME='kinoton'
$env:DB_PASSWORD='kinoton'
$env:STORAGE_ROOT='C:\PersonalProject\kinoton\storage'
```

## Verification

```powershell
.\gradlew.bat test
.\gradlew.bat bootRun --args='--spring.profiles.active=local'
```

## Local Accounts

`local` profile seed가 아래 검증 계정을 생성합니다. 비밀번호는 모두 `kinoton`입니다.

- `admin@kinoton.local`: 관리자
- `executive@kinoton.local`: 임원 모니터링
- `de.user@kinoton.local`: DE 사업본부 담당자

## Signup

`/signup`에서 회원가입 신청을 할 수 있습니다. 가입 신청 계정은 기본적으로 비활성 상태로 생성되며, 관리자가 `/users`에서 계정을 활성화하고 역할 및 사업본부 권한을 부여해야 로그인할 수 있습니다.
