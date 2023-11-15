package edu.tinkoff.ninjamireaclone.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "section")
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "section_seq")
    @SequenceGenerator(name = "section_seq", sequenceName = "section_seq", allocationSize = 1)
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Section section;

    @OneToMany(
            mappedBy = "section",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.EAGER
    )
    private List<Section> subsections;
}
