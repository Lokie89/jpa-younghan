# 자바 ORM 표준 JPA 프로그래밍
[1-1 SQL을 직접 다룰 때 발생하는 문제점](#1-1-SQL을-직접-다룰-때-발생하는-문제점)
# 1-1 SQL을 직접 다룰 때 발생하는 문제점

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

# 1-2 패러다임의 불일치

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
