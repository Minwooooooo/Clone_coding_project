package com.sparta.clone.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {
    @Id
    private String id;

    @Column
    private String name;

    @Column
    private int gender;

    @Column
    private int birthyear;

    @Column
    private String userRole;

}
