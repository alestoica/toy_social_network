package org.example.repository.db;

import org.example.domain.Message;
import org.example.repository.paging.Page;
import org.example.repository.paging.Pageable;
import org.example.repository.paging.PagingRepository;
import org.example.validators.MessageValidator;

public class MessageDBPagingRepository extends MessageDBRepository implements PagingRepository<Long, Message> {
    public MessageDBPagingRepository(String url, String username, String password, MessageValidator validator, UserDBRepository userDBRepository) {
        super(url, username, password, validator, userDBRepository);
    }

    @Override
    public Page<Message> findAll(Pageable pageable) {
        return null;
    }
}
