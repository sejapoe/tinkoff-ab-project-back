package edu.tinkoff.ninjamireaclone.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public final class Rights {
    @Column(name = "create_subsections", nullable = false)
    private Boolean createSubsections;
    @Column(name = "create_topics", nullable = false)
    private Boolean createTopics;
}
