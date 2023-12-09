package edu.tinkoff.ninjamireaclone.model.audit;

import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.context.SecurityContextHolder;

public class PostRevisionListener implements RevisionListener {
    @Override
    public void newRevision(Object o) {
        var revInfo = (RevInfo) o;
        revInfo.setUsername(SecurityContextHolder.getContext().getAuthentication().getName());
    }
}
