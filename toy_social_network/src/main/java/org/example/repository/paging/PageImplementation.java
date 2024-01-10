package org.example.repository.paging;
import java.util.stream.Stream;

public class PageImplementation<T> implements Page<T> {
    private Pageable pageable;
    private Stream<T> content;

    public PageImplementation(Pageable pageable, Stream<T> content) {
        this.pageable = pageable;
        this.content = content;
    }

    @Override
    public Pageable getPageable() {
        return this.pageable;
    }

    @Override
    public void nextPageable() {
        this.pageable.setPageNumber(this.pageable.getPageNumber() + 1);
    }

    @Override
    public void prevPageable() {
        this.pageable.setPageNumber(this.pageable.getPageNumber() - 1);
    }

    @Override
    public Stream<T> getContent() {
        return this.content;
    }
}
