# 자바 ORM 표준 JPA 프로그래밍

- [1-1 SQL을 직접 다룰 때 발생하는 문제점](#1-1-SQL을-직접-다룰-때-발생하는-문제점)
- [1-2 패러다임의 불일치](#1-2-패러다임의-불일치)
    - [지연로딩은 ***왜*** 사용하는가 ?](#지연로딩은-***왜***-사용하는가-?)
- [1-3 JPA 란 무엇인가?](#1-3-JPA-란-무엇인가?)
- [2-1 JPA 설정](#2-1-JPA-설정)
    - [***dialect*** 란 무엇인가?](#***dialect***-란-무엇인가?)
- [2-6 EntityManager](#2-6-EntityManager)
    - [***JPQL*** 이란?](#***JPQL***-이란?)
    - [EntityManager 의 ***동시성***](#EntityManager-의-***동시성***)
    - [EntityManager 와 ***Transaction***](#EntityManager-와-***Transaction***)
    - [영속성 컨텍스트 (Persistence Context)](#영속성-컨텍스트-(Persistence-Context))

### 1-1 SQL을 직접 다룰 때 발생하는 문제점

##### SQL 을 매핑시켜 객체를 만들 때 개발자가 작성해야 하는 것은 보통

    1. 해당 데이터를 매핑시킬 객체
    2. jdbc api 를 통해 CRUD 할 수 있는 객체
    3. SQL 들

만약 SQL 을 직접 다뤘을 때 변경해야 할 내용들이 이 모두라는 뜻이다.

##### 매핑 객체에 필드가 추가되었을 경우

    1. 매핑 객체의 클래스에 필드를 하나 추가한다.
    2. jdbc api 를 사용하는 객체에서 해당 테이터를 set 하는 로직을 추가한다.
    3. SQL 모두에 추가되는 데이터를 삽입한다.

##### 매핑 객체에 다른 객체를 필드로 추가되었을 경우

    1. 매핑 객체의 클래스에 필드를 추가한다.
    2. 추가된 필드의 클래스를 정의.
    3. jdbc api 를 사용하는 객체에서 해당 데이터를 가져와 조회하여 set 하는 로직을 추가한다.
    4. SQL 모두에 추가되는 추가된 객체의 키값을 가져오는 내용 삽입.
    5. jdbc api 에서 키값으로 재 조회할 로직 생성
    6. 새로운 테이블을 위한 SQL 작성

- DB - SQL - JDBC - OBJECT 에서 SQL 문을 직접 작성하여<br> JDBC 를 통해 DB 에 전달한다는 데에 문제가 있다.
- 결국 SQL 에 대한 의존도가 높아 필드 하나만 추가되어도<br> 해당 객체에 관련된 모든 SQL 문을 수정할 일이 생길 수 있다.


- JPA 이런 문제를 해결하기 위해<br> DB - SQL - JDBC - JPA - OBJECT
  <br> JPA 가 제공하는 API 를 사용하고 해당 API 는 SQL 을 생성하여 DB 로 전달한다.
  <br> 사용자 입장에선 따로 SQL 문들 만들 필요가 없다는 점이다.

이렇게 SQL 에 대한 **의존성을 낮춘다**.

### 1-2 패러다임의 불일치

##### 자바와 RDB 의 패러다임

    자바는 객체지향적 언어다.
    그래서 상속 다형성 추상화라는 객체지향적 언어의 특성에 부합한다.
    관계형 데이터베이스는 데이터 중심으로 구조화 되어있다
    그래서 둘은 지향하는 바가 다르다. 를 얘기하는 것 같다.

##### 상속 표현

    자바는 extends 를 통하여 클래스를 정의하고
    해당 필드값을 삽입한다.

```java
abstract class Item {
    Long id;
    String name;
    int price;
}

class Album extends Item {
    String artist;
}

class Movie extends Item {
    String director;
    String actor;
}

class Book extends Item {
    String author;
    String isbn;
}
```

    위와 같은 표현에서 Album, Movie, Book 모두 Item 의 필드인 id, name, price 를 가지고 있다.
    만약 SQL 문을 통하여 Album 을 삽입한다고 하면
    INSERT ALBUM ~~
    INSERT ITEM ~~~ 을 통하여 두 테이블 모두 삽입, 조회 등을 해야 한다.
    RDB 가 상속을 지원하지 않는 탓이다.

**그러나 JPA 는 이를 해결해 준다.**

- ``jpa.persist(album)`` : 앨범을 저장한다. 이는 두 SQL 문을 자동으로 생성하여 DB에 보내준다.
- ``jpa.find(Album.class, albumId)`` : 앨범을 조회한다. 이는 **JOIN** 을 포함한 SELECT 문을 DB 에 보내어 데이터를 추출한다.

이러한 방법을 통해 자바와 RDB 의 다른 패러다임을 해결해 준다.

##### 연관관계 표현

    자바 객체는 참조를 통하여 다른 객체와 연관관계를 갖는다

```java
class Member {
    Team team;

    Team getTeam() {
        return team;
    }
}

class Team {

}
```

    이렇게 Member 와 Team 이 연관관계를 맺고 있다고 하면
    DB에서는 Member 테이블에 Team 의 Foreign key 를 포함한 형태로 구성된다.
    따라서 JOIN 을 통하여 해당 Member 안의 Team 객체에 필요한 데이터를 조회할 수 있다.
    그리고 가져온 데이터를 다시 Team 객체로 변환하는 과정이 필요하다.
    
    저장할 때에도 마찬가지다.
    실제 Member 테이블에는 Team 의 FK 가 있기 때문에 Member 를 저장할 때는
    team 객체의 id 값을 가져와 넣어줘야 한다.

**그러나 JPA 는 이를 해결해 준다.**

- ``member.setTeam(team)`` : 회원과 팀 연관관계 설정 <br> ``jpa.persist(member)`` : 회원과 연관관계 함께 저장
- ``Member member = jpa.find(Member.class, memberId)`` : 회원 데이터 조회 <br> ``Team team = member.getTeam()`` : 회원에서 팀 추출

##### 연관관계 표현2

    만약 객체 연관관계가 복잡하다고 하자.
    한 객체에 여러 참조들이 있으며 그 참조들의 객체값에도 다른 참조들이 포함 되어있다.
    SQL 문을 다루게 되면 해당 참조내역의 데이터들을 조회하기 위해선
    해당하는 모든 테이블을 JOIN 해야 한다.
    
    또는 어떤 경우에는 이 객체, 또 어떤 경우에는 저 객체만 사용할 경우가 있을 수 있으므로
    매 기능 때마다 모든 테이블을 가져오는 것 또한 리소스 낭비가 될 수 있다.
    그래서 대부분 경우마다 필요한 SQL 문을 모두 작성한다.
    아주 큰 낭비이다.

**그러나 JPA 는 이를 해결해 준다.**

```java
class Service {
    public Order getMemberOrder() {
        Member member = jpa.find(Member.class, memberId);

        Order order = member.getOrder();
        order.getOrderDate(); // Order 를 사용하는 시점에 SELECT ORDER 를 실행
    }
}
```

    위와 같이 실제 Member 를 find 할 때는 Order 테이블을 조회하지 않았다가
    Order 가 가지고 있는 참조를 사용하게 될 경우에 SELECT 를 통하여 데이터를 조회한다.

#### 이를 객체가 사용될 때까지 조회를 미룬다고 해서 ***지연로딩*** 이라고 한다.

##### 비교 표현

    DB 테이블의 한 로우의 데이터를 가지고 비교를 한다고 하자
    DAO 코드를 작성하고

```java
class MemberDao {
    public Member getMember(String memberId) {
        String sql = "SELECT * FROM MEMBER WHERE MEMBER_ID = ?";
        // ... 데이터 가져와서 넣음
        return new Member(memberId, name);
    }

    public boolean 멤버_비교() {
        String memberId = "100";
        Member member1 = getMember(memberId);
        Member member2 = getMember(memberId);
        return member1 == member2;
    }
}
```

    멤버_비교 메서드는 같은 memberId 를 넣어 조회한 Member 객체지만
    실제 비교에서는 false 값을 내놓는다.
    데이터는 같은 데이터를 조회했지만, 객체 입장에서는 다른 객체이기 때문이다.

    이런 불일치를 해결하기 위해서 개발자는 또 중간 역할을 해줘야 한다.
    예를 들면 데이터를 컬렉션에 담아 같은 인덱스를 뽑아온다던지,
    동등성 비교를 위한 equals, hashCode 메서드를 오버라이드 한다던지?

**그러나 JPA 는 이를 해결해 준다.**

```java
class MemberDao {
    public boolean 멤버_비교() {
        String memberId = "100";
        Member member1 = jpa.find(Member.class, memberId);
        Member member2 = jpa.find(Member.class, memberId);
        return member1 == member2;
    }
}
```

JPA 가 ``같은 트랜잭션일 때`` 같은 객체가 조회되는 것을 보장해준다.

#### 정리

    RDB 와 객체 사이에는 다른점이 존재한다.
    그 다른점을 보완해주기 위해 개발자는 데이터를 객체로, 객체를 데이터로 고치기 위한 로직을 만들어야 한다.
    1. 상속을 지원해준다.
    2. 객체사이의 연관관계를 지원해준다.
    3. 객체안의 객체의 연관관계도 지원해준다. 이는 지연로딩이라는 방법을 통한다. ( 리소스를 덜 잡아먹으려고 ? )
    4. 객체와 데이터간의 동일성을 해결해준다. ( id 만 이겠죠 ? )

#### 지연로딩은 ***왜*** 사용하는가 ?

- 객체의 연관관계가 많을 수록, 또 후에 늘어날 가능성이 많으므로<br>
  한꺼번에 가져오는 것은 리소스의 부담을 가할 수 있기 때문에 [출처](https://velog.io/@bread_dd/JPA는-왜-지연-로딩을-사용할까)

### 1-3 JPA 란 무엇인가?

- JAVA 진영의 ORM 기술 표준 인터페이스
    - ORM : Object 객체와 Relational 관계를 Mapping 맺어줌.
- JAVA -(``명령``)-> JPA -(``SQL``)-> JDBC -(``SQL``)-> DB
- JAVA <-(``패러다임 극복, 객체매핑``)- JPA <-(``데이터``)- JDBC <-(``데이터``)- DB
- JPA 가 하는일
    - Entity 분석
    - SQL 생성
    - [패러다임 불일치](#1-2-패러다임의-불일치) 해결
    - 리턴값이 있을 경우 ResultSet 을 통한 객체 매핑
- 왜 탄생했는가
    - EJB 라는 기술 안에 엔티티 빈이라는 ORM 이 있었지만, 아주 복잡하고 J2EE 의존성이 높음<br>
      다음 주자인 hibernate 가 출현, 그를 기반으로 JPA 를 만들게 됨
- 왜 쓰는가
    - SQL 의존성 감소
        - SQL 만들어서 JDBC 에 전달 로직 안 만들어도 됨
        - 필드 추가 삭제 시 SQL 수정 안해도 됨
    - 패러다임 불일치 해결
    - 최적화 ( 뒤에 나옴 )
    - 데이터 베이스마다 다른 SQL 문을 익히지 않아도 됨

### 2-1 JPA 설정

- gradle 설정
    - hibernate core
    - hibernate entity manager
    - hibernate jpa
    - h2database
- jpa 설정 값을 담은 persistence.xml
    - jdbc driver
    - jdbc user
    - jdbc password
    - jdbc url
    - hibernate dialect

#### ***dialect*** 란 무엇인가?

    JPA 의 구현체는 명령을 SQL 문으로 바꾸어 jdbc 에게 전달합니다.
    이 때 전달 받을 DB 에 따라서 SQL 문이 다르게 작용할 수 있습니다.
    DB 마다 SQL 정책이 다르기 떄문입니다.
    예로 MYSQL 은 VARCHAR, ORACLE 은 VARCHAR2 를 사용한다거나,
    SQL 표준은 SUBSTRING, ORACLE 은 SUBSTR 등을 사용하는 경우입니다.
    이런 경우를 위해 JPA 구현체는 이를 DB 에 맞게 변환 시킬 수 있는
    dialect 정책을 가지고 있습니다.
    설정에서 사용하는 데이터 베이스에 대한 dialect 정책만 변경해주면 됩니다.

### 2-6 EntityManager

- 엔티티매니저는 JPA 의 대부분 기능을 제공한다.
    - 엔티티를 관리할 수 있다. (영속성 컨텍스트)
    - 트랜잭션 생성
        - 항상 트랜잭션 안에서 데이터 수정

```java
public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            logic(em);
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }
}
```

- 엔티티매니저는 EntityManagerFactory 로부터 만들어 진다.
    - 속성이 충족된 공장에서 엔티티매니저를 찍어낸다.
    - 공장은 하나만 만들길 추천한다. (비용)
- 스레드간의 엔티티매니저 공유는 안된다.

- Tx begin -(``tx 시작``)-> EntityManager -(``Entity 분석 및 SQL 생성, 보관``)-> Tx commit -(`` tx 커밋, SQL``)-> DB

#### ***JPQL*** 이란?

    JPA 를 통하여 사용하는 '객체' 를 대상으로 한 SQL 문
    기본적인 SQL 문과 대부분 비슷하다.
    하지만 위에서 말했듯이 사용하는 Entity 객체를 기준으로 쿼리를 작성한다
    SELECT m FROM Member m 일때
    Member 는 테이블 MEMBER 가 아닌 객체 Member 이다.
    사용법은 하면서 익히자.

#### EntityManager 의 ***동시성***

- EntityManager 는 다른 스레드 사이에서 공유해선 안 됨.
    - EntityManager 는 스레드 세이프 하지 않다.
    - EntityManager 사용 시 예외가 발생하면 롤백 한다.
        - 만약 method1 스레드와 method2 스레드가 같은 manager 를 사용할 때<br>
          method1 이 완벽히 끝나더라도 method2 가 예외를 발생시키면 둘다 안됨.
    - 영속성 컨텍스트가 가지고 있는 캐시가 OutOfMemoryException. ( 이건 뒤에 다시 )
- [출처](https://medium.com/@SlackBeck/jpa-entitymanager와-동시성-e30f841fcdf8)

#### EntityManager 와 ***Transaction***

- Transaction 을 Entity 에서 가져옴
    - tx 는 시작, 커밋, 롤백 3가지를 사용하는데<br>
      시작 후 커밋까지 Entity 에 내리는 명령들을 모아둠<br>
      커밋 시 모아둔 SQL 명령어들을 DB에 보냄 ( 지연 로딩 )

#### 영속성 컨텍스트 (Persistence Context)

- Entity 를 영구 저장하는 환경
    - DB 와 연결? 되어있는 객체를 관리 하는 곳
        - 영속 ( 연결 됨 )
        - 비영속 ( 연결 안 됨, 그냥 객체일 뿐 )
        - 준영속 ( 연결 해지, 얘도 그냥 객체 )
        - 삭제 ( 연결 되어 있지만 DB 에 삭제 됨 )
- 캐시 기능 사용 가능
    - 같은 데이터를 조회할 때 굳이 DB 에 갈 필요 없음
- 더티 체킹 가능
    - 객체가 변경 됐을 때 저장 명령어를 주지 않아도 알아서 update 쿼리를 날림
    - 스냅샷 을 저장해놓고 그 와 다르면 그 부분을 update 함
- 지연 로딩

### 4. 엔티티 매핑

- @Entity
    - 기본 생성자
    - 저장할 필드 final 안됨
- @Table
    - 테이블 이름
- @Enumerated
    - enum 객체 사용 시
- @Temporal
    - 날짜 타입