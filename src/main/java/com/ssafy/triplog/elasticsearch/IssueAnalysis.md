# 🛠 Elasticsearch 연동 이슈 해결 정리

프로젝트에서 Elasticsearch 연동 과정 중 발생했던 주요 문제들과 해결 방법을 기록합니다.

---

## 🔍 문제 분석 및 해결 단계

### ✅ 1. `403 Forbidden` 에러

- **문제**  
  `JWTFilter`에서 `/api/elastic/**` 경로를 인증 제외 대상에 포함했지만, **context path (`/triplog`)** 가 고려되지 않아 필터링되지 않음

- **문제 코드 예시**
  ```java
  "/api/elastic/**"  // context path 누락
  ```

- **해결 방법**
  ```java
  String fullUri = request.getRequestURI();      // 예: /triplog/api/elastic/...
  String contextPath = request.getContextPath(); // 예: /triplog
  String servletPath = fullUri.substring(contextPath.length()); // 결과: /api/elastic/...
  ```

---

### ✅ 2. `LocalDateTime` 직렬화 오류 (Jackson)

- **문제**  
  `LocalDateTime` 타입을 JSON으로 변환 시 다음과 같은 오류 발생:

  ```
  Java 8 date/time type `java.time.LocalDateTime` not supported by default
  ```

- **해결 방법**  
  Jackson에 JavaTimeModule 등록:

  ```java
  private final ObjectMapper objectMapper = new ObjectMapper()
      .registerModule(new JavaTimeModule()) // ⭐ 핵심!
      .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  ```

- **추가**  
  또는 `build.gradle`에 다음 의존성 추가:

  ```groovy
  implementation 'com.fasterxml.jackson.datatype:jackson-datatype-jsr310'
  ```

---

### ✅ 3. Elasticsearch 응답 파싱 오류 (NullPointerException)

- **문제**  
  인덱스가 존재하지 않아 다음과 같은 예외 발생:

  ```
  ElasticsearchStatusException: reason=no such index [plan_post_search]
  ```

- **원인**
    - 인덱스가 존재하지 않음
    - 응답 파싱 도중 NullPointerException 발생

- **해결 방법**
    - 인덱스 존재 여부 체크 후 없으면 자동 생성:

      ```java
      if (!indices.exists(request, RequestOptions.DEFAULT)) {
          createIndex();
      }
      ```

    - 인덱스 매핑을 명확히 정의하여 문서 구조 설정

---

## 🎯 핵심 요약

- Elasticsearch는 **인덱스 없이도 데이터를 수신**할 수 있지만, **정상적인 응답을 보장하지 않음**
- Spring Security의 경로 매칭 시 **contextPath 고려 필수**
- Jackson은 **LocalDateTime 직렬화 시 JavaTimeModule 필수**

---

## 🔧 적용 순서 요약

1. ✅ `JWTFilter` 경로 매칭 로직 수정 → `403` 문제 해결
2. ✅ Jackson 설정 (`JavaTimeModule`) 추가 → `LocalDateTime` 직렬화 오류 해결
3. ✅ Elasticsearch 인덱스 자동 생성 처리 → 응답 파싱 예외 해결
