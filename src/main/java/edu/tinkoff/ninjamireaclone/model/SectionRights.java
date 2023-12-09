package edu.tinkoff.ninjamireaclone.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
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
    @JoinColumn(name = "privilege_id")
    private Privilege privilege;

    @Embedded
    private Rights rights;
}
