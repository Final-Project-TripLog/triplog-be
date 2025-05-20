
# 🔐 Spring Security + JWT 인증 흐름 정리

## 📁 1. 파일 구성 및 역할

### 🛠️ 설정 파일

#### `SecurityConfig.java`
- **역할:** Spring Security의 핵심 설정을 담당
- **주요 기능:**
    - 보안 필터 체인 구성
    - 경로별 접근 권한 설정
    - JWT 필터 및 로그인 필터 등록
    - 인증 관리자 설정
    - CORS 설정

#### `CorsMvcConfig.java`
- **역할:** CORS(Cross-Origin Resource Sharing) 설정
- **주요 기능:**
    - 모든 URL 패턴에 대한 CORS 정책 구성
    - 허용할 Origin, Method, Header 등 설정

---

### 🔐 JWT 관련 파일

#### `JWTUtil.java`
- **역할:** JWT 생성 및 검증 유틸리티
- **주요 기능:**
    - JWT 토큰 생성
    - 사용자 이메일 및 권한 추출
    - 만료 여부 검증

#### `JWTFilter.java`
- **역할:** 보호된 API 요청 시 JWT를 검증하는 필터
- **주요 기능:**
    - 요청 헤더에서 토큰 추출 및 검증
    - 토큰에서 사용자 정보 추출
    - SecurityContext에 인증 정보 저장

#### `LoginFilter.java`
- **역할:** 로그인 요청 처리 및 JWT 발급
- **주요 기능:**
    - 이메일/비밀번호 추출 및 인증
    - 인증 성공 시 JWT 발급 후 응답 헤더에 추가

---

### 👤 사용자 인증 관련 파일

#### `CustomUserDetails.java`
- **역할:** `UserDetails` 구현체 (Spring Security 인증 정보)
- **주요 기능:**
    - 사용자 정보 및 권한, 계정 상태 반환

#### `CustomUserDetailsService.java`
- **역할:** 사용자 인증에 필요한 정보를 DB에서 불러오는 서비스
- **주요 기능:**
    - 이메일로 사용자 조회
    - `CustomUserDetails` 객체로 변환

---

### 🧩 사용자 서비스 및 리포지토리

#### `UserService.java`
- **역할:** 사용자 관련 비즈니스 로직 처리
- **주요 기능:**
    - 회원가입 처리
    - 비밀번호 암호화
    - 사용자 정보 조회

#### `UserRepository.java`
- **역할:** 사용자 DB 접근
- **주요 기능:**
    - 사용자 저장, 조회, 수정

---

## 🔄 2. 주요 인증 흐름

### 🚀 애플리케이션 시작 시

1. `SecurityConfig`가 로드되어 보안 필터 체인 초기화
2. `filterChain()`에서 각종 보안 필터 등록
3. `JWTUtil`이 `jwt.secret` 값을 기반으로 서명 키 생성
4. `CustomUserDetailsService`가 인증 로직에 등록

---

### 📝 회원가입 흐름

1. 클라이언트 → `POST /api/users/signup`
2. `UserController.signup()` → `UserService.registerUser()` 호출
3. 중복 이메일 체크 → 비밀번호 암호화 → 권한 설정 → DB 저장
4. 성공 시 사용자 정보 일부 반환

---

### 🔑 로그인 흐름

1. 클라이언트 → `POST /api/users/login`
2. `LoginFilter`가 요청을 가로채고 `attemptAuthentication()` 수행
3. `AuthenticationManager` → `CustomUserDetailsService.loadUserByUsername()` 호출
4. 인증 성공 → `successfulAuthentication()`에서 JWT 생성 및 응답
5. 실패 시 401 에러 반환

---

### 🔒 보호된 API 접근 흐름

1. 클라이언트 → `/api/myplans` 등의 API 요청
2. 요청 헤더에 `"Authorization: Bearer {token}"` 포함
3. `JWTFilter`가 요청 가로채어 다음 수행:
    - 토큰 추출 → 유효성 검사 → 사용자 정보 추출
    - `SecurityContext`에 인증 객체 등록
    - 다음 필터로 전달

4. 컨트롤러 실행 전 `SecurityConfig`의 권한 정책에 따라 접근 제어

---

### 👤 인증된 사용자 정보 사용

- `@AuthenticationPrincipal` 또는 `Authentication` 객체로 접근 가능
  ```java
  @GetMapping("/me")
  public ResponseEntity<?> getUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
      return ResponseEntity.ok(userDetails.getUsername());
  }
  ```

---

## 🔗 3. 전체 흐름 요약

```
Spring Boot 시작
 → SecurityConfig 로드
   → 필터 체인 구성 (JWTFilter, LoginFilter)
 → JWTUtil 초기화

회원가입
 → UserController → UserService → UserRepository → DB 저장

로그인
 → HTTP 요청
   → LoginFilter
     → AuthenticationManager → CustomUserDetailsService
       → DB 사용자 조회 → 인증 → JWT 발급

API 요청
 → HTTP 요청
   → JWTFilter
     → 토큰 유효성 검사
     → SecurityContext 설정
 → 권한 검사 → 컨트롤러 진입
```

---

