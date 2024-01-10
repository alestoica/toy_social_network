package org.example.service;

import org.example.domain.Request;
import org.example.repository.db.RequestDBPagingRepository;
import org.example.repository.db.RequestDBRepository;
import org.example.repository.paging.Page;
import org.example.repository.paging.Pageable;

public class PagingRequestService extends RequestService implements PagingServiceInterface<Long, Request> {
    public PagingRequestService(RequestDBPagingRepository repo) {
        super(repo);
    }

    @Override
    public Page<Request> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }
}
