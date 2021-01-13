package jpabook.ch05;

import javax.persistence.*;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}
