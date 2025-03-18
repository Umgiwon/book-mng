# CDRI Book Management System

## 프로젝트 개요
CDRI Book Management System은 도서 관리 기능을 제공하는 Spring Boot 기반 애플리케이션입니다. JPA와 QueryDSL을 활용하여 데이터 액세스를 효율적으로 수행하며, Swagger를 통한 API 문서화를 지원합니다.

## 기술 스택
- **Spring Boot** 3.2.4
- **Java** 17
- **JPA (Java Persistence API)**
- **QueryDSL** 5.0.0
- **MariaDB** (데이터베이스)
- **Lombok** (코드 간소화)
- **Swagger (SpringDoc)** 2.2.0
- **P6Spy** (SQL 로깅)

## 프로젝트 설정
### 1. Build 설정 (Gradle)

프로젝트는 Gradle을 사용하여 빌드됩니다. 주요 dependencies는 다음과 같습니다:

- **Spring Boot Starters**: `spring-boot-starter-data-jpa`, `spring-boot-starter-web`, `spring-boot-starter-validation`
- **QueryDSL**: `querydsl-jpa`, `querydsl-apt`
- **Swagger**: `springdoc-openapi-starter-webmvc-ui`
- **P6Spy**: SQL 로깅을 위한 `p6spy-spring-boot-starter`
- **테스트 라이브러리**: `spring-boot-starter-test`

### 2. 애플리케이션 설정 (application.yml)

애플리케이션의 설정은 `application.yml`을 통해 관리됩니다.

#### 서버 설정
- 기본 포트: `8080`

#### 데이터베이스 설정
- **DBMS**: MariaDB
- **JDBC URL**: `jdbc:mariadb://localhost:3306/bookmng`
- **사용자**: `user`
- **비밀번호**: `1234`

#### JPA 설정
- Hibernate DDL 자동 생성 옵션: `update`
- Naming 전략: `PhysicalNamingStrategyStandardImpl`
- SQL 방언: `MariaDBDialect`
- SQL 포맷팅 및 로깅 설정
    - Hibernate의 `show_sql`은 `false`로 설정하고, P6Spy를 활용하여 SQL 로그를 출력

#### 로깅 설정
- Hibernate, HikariCP, P6Spy에 대한 로깅 레벨 지정
    - `org.hibernate.type.descriptor.sql.BasicBinder: TRACE`
    - `com.zaxxer.hikari.HikariDataSource: DEBUG`
    - `com.p6spy.engine.spy.P6SpyDriver: DEBUG`

#### Swagger 설정
- API 문서 URL: `http://localhost:8080/swagger-ui.html`
- API 문서 제목: `CDRI-Book-Management-System API 문서`
- API 설명: `카테고리 별로 분류 가능한 도서 관리 시스템`
- 버전: `1.0`
- 패키지 스캔 경로: `com.cdri.bookmng`

## 실행 방법
1. **Gradle 빌드**
   ```sh
   ./gradlew build
   ```
2. **애플리케이션 실행**
   ```sh
   java -jar build/libs/book-mng-0.0.1-SNAPSHOT.jar
   ```
3. **Swagger API 문서 확인**
    - `http://localhost:8080/swagger-ui.html`
