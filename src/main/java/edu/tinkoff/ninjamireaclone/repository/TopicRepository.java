package edu.tinkoff.ninjamireaclone.repository;

import edu.tinkoff.ninjamireaclone.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long>, QuerydslPredicateExecutor<Topic> {
}
