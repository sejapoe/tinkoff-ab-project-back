package edu.tinkoff.ninjamireaclone.repository;

import edu.tinkoff.ninjamireaclone.model.RuleSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface RuleSetRepository extends JpaRepository<RuleSet, Long>, QuerydslPredicateExecutor<RuleSet> {
}
