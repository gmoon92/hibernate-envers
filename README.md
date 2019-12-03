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
