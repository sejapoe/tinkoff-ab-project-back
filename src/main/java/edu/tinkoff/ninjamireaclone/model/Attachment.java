package edu.tinkoff.ninjamireaclone.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@IdClass(AttachmentId.class)
@Getter
@Setter
@Table(name = "attachment")
public class Attachment {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "post_id")
    @Id
    private Post post;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "document_id")
    @Id
    private Document document;
}
