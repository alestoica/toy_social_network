package org.example.repository.paging;
public interface Pageable {
    int getPageNumber();
    void setPageNumber(int number);
    int getPageSize();
    void setPageSize(int size);
}
