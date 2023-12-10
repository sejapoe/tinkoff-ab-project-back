package edu.tinkoff.ninjamireaclone.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Audited
@AuditTable(value = "post_audit")
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Table(name = "post")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "post_seq")
    @SequenceGenerator(name = "post_seq", sequenceName = "post_seq", allocationSize = 1)
    private Long id;

    @Column(name = "text")
    private String text;

    @NotAudited
    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @NotAudited
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Account author;

    @NotAudited
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Topic parent;

    @NotAudited
    @Column(name = "is_anonymous", nullable = false)
    private boolean isAnonymous;

    @NotAudited
    @Column(name = "op", nullable = false)
    private boolean isOpening;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "attachment",
            joinColumns = {@JoinColumn(name = "post_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "document_id", referencedColumnName = "id")}
    )
    private Set<Document> documents = new HashSet<>();

    public void addDocument(Document document) {
        this.documents.add(document);
        document.getPosts().add(this);
    }

    public void removeDocument(Document document) {
        this.documents.remove(document);
        document.getPosts().remove(this);
    }
}
