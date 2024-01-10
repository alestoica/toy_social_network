package org.example.service;

import org.example.domain.User;
import org.example.repository.db.UserDBPagingRepository;
import org.example.repository.paging.Page;
import org.example.repository.paging.Pageable;

public class PagingUserService extends UserService implements PagingServiceInterface<Long, User>{
    public PagingUserService(UserDBPagingRepository repo) {
        super(repo);
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }
}
