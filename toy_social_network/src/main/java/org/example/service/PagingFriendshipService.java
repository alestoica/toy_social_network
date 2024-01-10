package org.example.service;

import org.example.domain.Friendship;
import org.example.domain.Tuple;
import org.example.repository.db.FriendshipDBPagingRepository;
import org.example.repository.db.FriendshipDBRepository;
import org.example.repository.paging.Page;
import org.example.repository.paging.Pageable;

public class PagingFriendshipService extends FriendshipService implements PagingServiceInterface<Tuple<Long, Long>, Friendship> {
    public PagingFriendshipService(FriendshipDBPagingRepository repo) {
        super(repo);
    }

    @Override
    public Page<Friendship> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }
}
