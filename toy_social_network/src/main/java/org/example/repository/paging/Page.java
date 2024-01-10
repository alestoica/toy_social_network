package org.example.repository.paging;
import java.util.stream.Stream;

public interface Page<E> {
    Pageable getPageable();
    void nextPageable();
    void prevPageable();
    Stream<E> getContent();
}
