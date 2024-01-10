package org.example.service;

import org.example.domain.Friendship;
import org.example.domain.Message;
import org.example.domain.Tuple;
import org.example.domain.User;
import org.example.repository.db.MessageDBPagingRepository;
import org.example.repository.db.MessageDBRepository;
import org.example.repository.paging.Page;
import org.example.repository.paging.Pageable;
import org.example.utils.events.ChangeEventType;
import org.example.utils.events.FriendshipChangeEvent;
import org.example.utils.events.MessageChangeEvent;
import org.example.utils.observer.Observable;
import org.example.utils.observer.Observer;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

public class MessageService extends Service<Long, Message> implements Observable<MessageChangeEvent> {
    private ArrayList<Observer<MessageChangeEvent>> observers = new ArrayList<>();

    MessageDBPagingRepository repo;

    public MessageService(MessageDBPagingRepository repo) {
        super(repo);
        this.repo = repo;
    }

    @Override
    public Optional<Message> delete(Long id) {
        Optional<Message> deleted = super.delete(id);
        deleted.ifPresent(message -> notifyObservers(new MessageChangeEvent(ChangeEventType.DELETE, message)));
        return deleted;
    }

    @Override
    public Optional<Message> save(Message message) {
        Optional<Message> saved = super.save(message);
        if(saved.isEmpty())
            notifyObservers(new MessageChangeEvent(ChangeEventType.ADD, message));
        return saved;
    }

    public Page<Message> findAll(Long id1, Long id2, Pageable pageable) {
        return repo.findAll(id1, id2, pageable);
    }

    @Override
    public void addObserver(Observer<MessageChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<MessageChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(MessageChangeEvent t) {
        observers.forEach(x -> x.update(t));
    }
}
