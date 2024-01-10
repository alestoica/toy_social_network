package org.example.repository.paging;

public class PageableImplementation implements Pageable {
    private int pageNumber;
    private int pageSize;

    public PageableImplementation(int pageNumber, int pageSize) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    @Override
    public int getPageNumber() {
        return this.pageNumber;
    }

    @Override
    public void setPageNumber(int number) {
        this.pageNumber = number;
    }

    @Override
    public int getPageSize() {
        return this.pageSize;
    }

    @Override
    public void setPageSize(int size) {
        this.pageSize = size;
    }
}
