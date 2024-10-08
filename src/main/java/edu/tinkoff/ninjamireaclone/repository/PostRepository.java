package edu.tinkoff.ninjamireaclone.repository;

import edu.tinkoff.ninjamireaclone.model.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long>, QuerydslPredicateExecutor<PostEntity> {

    @Query(value = """
            WITH deleted AS (DELETE FROM post WHERE id in (
                SELECT id
                FROM post
                WHERE op=false and created_at + interval '2 years' < current_timestamp
                FETCH FIRST :amount ROWS ONLY
            ) RETURNING *)
            SELECT COUNT(*) FROM deleted;""",
            nativeQuery = true)
    long deleteComments(int amount);
}
