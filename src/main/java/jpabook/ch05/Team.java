package jpabook.ch05;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
