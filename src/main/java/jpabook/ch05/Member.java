package jpabook.ch05;

import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "MEMBER")
@Entity
public class Member {

    public Member() {
    }

    public Member(String id, String username) {
        this.id = id;
        this.username = username;
    }

    @Column(name = "MEMBER_ID")
    @Id
    private String id;

    private String username;

    @ManyToOne
    @JoinColumn(name = "TEAM_ID")
    private Team team;

}
