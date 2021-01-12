package jpabook.start;

import javax.persistence.*;

@Entity
@SequenceGenerator(name = "BOARD_SEQ_GENERATOR", sequenceName = "BOARD_SEQ", initialValue = 1, allocationSize = 1)
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BOARD_SEQ_GENERATOR")
    private Long id;
    @Column(name = "DATA")
    private String data;

    public Long getId() {
        return id;
    }

    public String getData() {
        return data;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setData(String data) {
        this.data = data;
    }
}
