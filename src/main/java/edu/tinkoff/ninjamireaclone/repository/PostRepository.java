package edu.tinkoff.ninjamireaclone.repository;

import edu.tinkoff.ninjamireaclone.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, QuerydslPredicateExecutor<Post> {
}
