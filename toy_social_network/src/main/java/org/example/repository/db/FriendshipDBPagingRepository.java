package org.example.repository.db;

import org.example.domain.Friendship;
import org.example.domain.Tuple;
import org.example.repository.paging.Page;
import org.example.repository.paging.PageImplementation;
import org.example.repository.paging.Pageable;
import org.example.repository.paging.PagingRepository;
import org.example.validators.FriendshipValidator;

import java.sql.*;
import java.util.ArrayList;

public class FriendshipDBPagingRepository extends FriendshipDBRepository implements PagingRepository<Tuple<Long, Long>, Friendship> {
    public FriendshipDBPagingRepository(String url, String username, String password, FriendshipValidator validator) {
        super(url, username, password, validator);
    }

    @Override
    public Page<Friendship> findAll(Pageable pageable) {
        return null;
    }
}
