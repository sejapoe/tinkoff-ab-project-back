package edu.tinkoff.ninjamireaclone.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "rule_set")
public class RuleSet {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rule_set_seq")
    @SequenceGenerator(name = "rule_set_seq", sequenceName = "rule_set_seq", allocationSize = 1)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @OrderBy("order asc")
    @OneToMany(mappedBy = "ruleSet", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Rule> rules;

    /**
     * @see edu.tinkoff.ninjamireaclone.service.rule.condition.ConditionResolver
     */
    @Column(name = "condition", nullable = false)
    private String condition;
}
