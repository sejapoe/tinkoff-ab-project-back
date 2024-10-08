package edu.tinkoff.ninjamireaclone.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Table(name = "document")
public class DocumentEntity {
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
    private Set<PostEntity> posts = new HashSet<>();
}
