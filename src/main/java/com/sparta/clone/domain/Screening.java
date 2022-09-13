package com.sparta.clone.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Screening {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "movie", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private CGVmovie movie;


    @JoinColumn(name = "cinema", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Cinema cinema;

    @Column
    private String date;

    @Column
    private String time;

    @Column
    private String booked;

    @OneToMany (fetch = FetchType.LAZY, mappedBy = "screening")
    private List<Ticketing> ticketings ;
}
