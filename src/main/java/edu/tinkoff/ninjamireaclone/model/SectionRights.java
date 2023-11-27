package edu.tinkoff.ninjamireaclone.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "section_rights")
public class SectionRights {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "section_rights_seq")
    @SequenceGenerator(name = "section_rights_seq", sequenceName = "section_rights_seq", allocationSize = 1)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "section_id")
    private Section section;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @Embedded
    private Rights rights;
//    @Column(name = "create_subsections", nullable = false)
//    private Boolean createSubsections;
//
//    @Column(name = "create_topics", nullable = false)
//    private Boolean createTopics;
}
