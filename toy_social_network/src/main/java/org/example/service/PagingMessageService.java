package org.example.service;

import org.example.domain.Message;
import org.example.repository.db.MessageDBPagingRepository;
import org.example.repository.db.MessageDBRepository;
import org.example.repository.paging.Page;
import org.example.repository.paging.Pageable;

public class PagingMessageService extends MessageService implements PagingServiceInterface<Long, Message> {
    public PagingMessageService(MessageDBPagingRepository repo) {
        super(repo);
    }

    @Override
    public Page<Message> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }
}
