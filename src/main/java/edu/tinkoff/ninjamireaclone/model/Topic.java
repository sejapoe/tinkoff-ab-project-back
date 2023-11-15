package edu.tinkoff.ninjamireaclone.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "topic")
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "topic_seq")
    @SequenceGenerator(name = "topic_seq", sequenceName = "topic_seq", allocationSize = 1)
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Section section;

    @OneToMany(
            mappedBy = "topic",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.EAGER
    )
    private List<Post> posts;
}
