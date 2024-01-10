package org.example.service;

import org.example.domain.Friendship;
import org.example.domain.Tuple;
import org.example.domain.User;
import org.example.repository.db.FriendshipDBPagingRepository;
import org.example.repository.db.FriendshipDBRepository;
import org.example.repository.paging.Page;
import org.example.repository.paging.Pageable;
import org.example.utils.events.ChangeEventType;
import org.example.utils.events.FriendshipChangeEvent;
import org.example.utils.events.UserChangeEvent;
import org.example.utils.observer.Observable;
import org.example.utils.observer.Observer;

import java.util.ArrayList;
import java.util.Optional;

public class FriendshipService extends Service<Tuple<Long, Long>, Friendship> implements Observable<FriendshipChangeEvent> {
    FriendshipDBPagingRepository repo;
    private ArrayList<Observer<FriendshipChangeEvent>> observers = new ArrayList<>();

    public FriendshipService(FriendshipDBPagingRepository repo) {
        super(repo);
        this.repo = repo;
    }

    public Page<Long> findAll(Long id, Pageable pageable) {
        return repo.findAll(id, pageable);
    }

    @Override
    public Optional<Friendship> save(Friendship friendship) {
        Optional<Friendship> saved = super.save(friendship);
        if(saved.isEmpty())
            notifyObservers(new FriendshipChangeEvent(ChangeEventType.ADD, friendship));
        return saved;
    }

    @Override
    public Optional<Friendship> delete(Tuple<Long, Long> id) {
        Optional<Friendship> deleted = super.delete(id);
        deleted.ifPresent(friendship -> notifyObservers(new FriendshipChangeEvent(ChangeEventType.DELETE, friendship)));
        return deleted;
    }

    @Override
    public void addObserver(Observer<FriendshipChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<FriendshipChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(FriendshipChangeEvent t) {
        observers.forEach(x -> x.update(t));
    }
}
