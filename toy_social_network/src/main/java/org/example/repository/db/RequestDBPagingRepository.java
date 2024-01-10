package org.example.repository.db;

import org.example.domain.Request;
import org.example.repository.paging.Page;
import org.example.repository.paging.Pageable;
import org.example.repository.paging.PagingRepository;
import org.example.validators.RequestValidator;

public class RequestDBPagingRepository extends RequestDBRepository implements PagingRepository<Long, Request> {
    public RequestDBPagingRepository(String url, String username, String password, RequestValidator validator, UserDBRepository userDBRepository) {
        super(url, username, password, validator, userDBRepository);
    }

    @Override
    public Page<Request> findAll(Pageable pageable) {
        return null;
    }
}
