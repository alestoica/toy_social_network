package org.example.service;

import org.example.domain.Entity;
import org.example.repository.paging.Page;
import org.example.repository.paging.Pageable;

public interface PagingServiceInterface<ID, E extends Entity<ID>> extends ServiceInterface<ID, E> {
    Page<E> findAll(Pageable pageable);
}
