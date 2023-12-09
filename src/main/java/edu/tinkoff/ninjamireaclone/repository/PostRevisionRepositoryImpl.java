package edu.tinkoff.ninjamireaclone.repository;

import edu.tinkoff.ninjamireaclone.model.Post;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.envers.AuditReaderFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class PostRevisionRepositoryImpl implements PostRevisionRepository {

    @PersistenceContext
    private EntityManager em;

    public List<Long> getRevisionsIds(Long id) {
        var auditReader = AuditReaderFactory.get(em);
        return auditReader.getRevisions(Post.class, id).stream().map(Number::longValue).toList();
    }


    public List<Post> getRevisionsById(Long id) {
        var revisionIds = getRevisionsIds(id);
        var result = new ArrayList<Post>();
        var auditReader = AuditReaderFactory.get(em);
        for (var revId : revisionIds) {
            var post = auditReader.find(Post.class, id, revId);
            result.add(post);
        }
        return result;
    }

}