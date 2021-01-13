package jpabook.ch05;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Table(name = "TEAM")
@Entity
public class Team {

    public Team() {

    }

    public Team(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Id
    @Column(name = "TEAM_ID")
    private String id;

    private String name;

    @OneToMany(mappedBy = "team")
    private List<Member> members = new ArrayList<>();

}
