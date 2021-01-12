package jpabook.start;

import javax.persistence.*;

@Entity
@Table(name = "MEMBER", uniqueConstraints = {@UniqueConstraint(name = "NAME_AGE_UNIQUE", columnNames = {"NAME", "AGE"})})
public class Member {
    @Id
    @Column(name = "ID")
    private String id;
    @Column(name = "NAME", nullable = false, length = 10)
    private String username;
    private Integer age;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getAge() {
        return age;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
