package edu.tinkoff.ninjamireaclone.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "document")
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "document_seq")
    @SequenceGenerator(name = "document_seq", sequenceName = "document_seq", allocationSize = 1)
    private Long id;

    @Column(name = "name", nullable = false)
    private String filename;

    @Column(name = "original_name", nullable = false)
    private String originalName;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private DocumentType documentType;

    @ManyToMany(mappedBy = "documents", fetch = FetchType.LAZY)
    private Set<Post> posts = new HashSet<>();
}
