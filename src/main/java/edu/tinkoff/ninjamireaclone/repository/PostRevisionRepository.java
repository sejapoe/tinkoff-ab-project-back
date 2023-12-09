package edu.tinkoff.ninjamireaclone.repository;

import edu.tinkoff.ninjamireaclone.model.Post;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRevisionRepository {

    List<Post> getRevisionsById(Long id);
}
