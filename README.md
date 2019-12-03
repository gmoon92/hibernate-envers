# hibernate-envers-rev-listener
Hibernate의 Envers는 어디까지 제어할 수 있을까?

# 개발 환경
- Java8
- Spring Boot 2.2.1.RELEAS
- Hibernate 5.4.4.Final
- Hibernate Envers 5.4.4.Final
- QueryDsl 4.2.1
- JUnit5(AssertJ), Maven
- H2 1.4.200

# 구현 기능
 - Revision Custom Entity
 - EntityTrackingRevisionListener
 - EventListenerRegistry
 - Spring Data JPA Repository Custom
 - h2 dev, test 환경 분리

# 들어가기전
# Hibernate Envers Concepts

_사용자들이 변경한 데이터들에 대해 조회할 수 있는 화면을 제공해주세요._

1. 화면의 타이틀은 "변경 이력 사항 조회"로 개발한다.
2. 기존 엔티티가 변경되면 추적할 수 있는 이력 테이블을 구성한다.
3. 이력 테이블에서 관리하는 데이터들을 사용자에게 제공한다.
4. 단, 해당 이력 데이터는 사용자가 정의한 데이터들만 관리한다.
5. 추가로 이전의 데이터와 최신 데이터를 비교하여 변경된 사항들을 조회할 수 있도록 한다.

# 프로젝트 설명

준비중...

---

# [번외] 구현에 하면서 발생했던 이슈들
- h2의 버전 문제로 인한 mem 옵션의 제약사항
- Spring Boot 2.0 부터 변경된 findOne 메서드, 그 이유와 사용법
- N+1 발생의 원인과 해결 방법들
  - 글로벌 패치 전략이 답이 아니다.
  - JPQL의 패러다임
- RevisionListener가 아닌 EntityTrackingRevisionListener를 사용한 이유
  - newRevision과 entityChanged 메서드의 차이
- EventListenerRegistry 이벤트 등록
  - POST_COMMIT_INSERT 과 POST_INSERT의 차이
  - ID 생성 전략과 Rollback 문제
