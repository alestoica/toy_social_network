package org.example.repository.paging;


import org.example.domain.Entity;
import org.example.repository.Repository;

public interface PagingRepository<ID, E extends Entity<ID>> extends Repository<ID, E> {
    Page<E> findAll(Pageable pageable);
}
