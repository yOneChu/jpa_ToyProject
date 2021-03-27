package com.jpabook.domain;



import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;

    @Embedded //내장타입
    private Address address;

    @JsonIgnore // JSON으로 변환 시 해당 컬럼을 뺀다
    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();

}
