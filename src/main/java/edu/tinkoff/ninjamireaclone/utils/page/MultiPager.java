package edu.tinkoff.ninjamireaclone.utils.page;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

@RequiredArgsConstructor
public class MultiPager<A1, A2, Repo1 extends JpaRepository<A1, ?> & QuerydslPredicateExecutor<A1>, Repo2 extends JpaRepository<A2, ?> & QuerydslPredicateExecutor<A2>> {
    private final Repo1 repo1;
    private final Repo2 repo2;

    public MultiPage<A1, A2> findAll(int pageNumber, int pageSize, Predicate predicate1, Predicate predicate2) {
        long count1 = repo1.count(predicate1);
        long count2 = repo2.count(predicate2);

        long totalElements = count1 + count2;

        int pagesCount = (int) (totalElements / pageSize) + (totalElements % pageSize != 0 ? 1 : 0);

        long offset = count1 % pageSize;
        int pagesFor1 = (int) (count1 / pageSize);

        List<A1> list1 = repo1.findAll(predicate1, OffsetPageable.of(pageNumber, pageSize)).stream().toList();
        List<A2> list2;
        if (list1.isEmpty()) {
            list2 = repo2.findAll(predicate2, OffsetPageable.of(pageNumber - pagesFor1, pageSize, offset)).stream().toList();
        } else if (list1.size() < pageSize) {
            list2 = repo2.findAll(predicate2, OffsetPageable.of(pageNumber - pagesFor1, pageSize - list1.size())).stream().toList();
        } else {
            list2 = List.of();
        }

        return new MultiPage<>(pageNumber, pageSize, pagesCount, totalElements, list1, list2);
    }
}
