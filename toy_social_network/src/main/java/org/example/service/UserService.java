package org.example.service;

import org.example.domain.User;
import org.example.repository.Repository;
import org.example.repository.db.UserDBPagingRepository;
import org.example.repository.db.UserDBRepository;
import org.example.repository.paging.Page;
import org.example.repository.paging.Pageable;
import org.example.repository.paging.PageableImplementation;
import org.example.repository.paging.PagingRepository;
import org.example.utils.events.ChangeEventType;
import org.example.utils.events.UserChangeEvent;
import org.example.utils.observer.Observable;
import org.example.utils.observer.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserService extends Service<Long, User> implements Observable<UserChangeEvent> {
    UserDBPagingRepository repo;
    private ArrayList<Observer<UserChangeEvent>> observers = new ArrayList<>();

    public UserService(UserDBPagingRepository repo) {
        super(repo);
        this.repo = repo;
    }

    public ArrayList<Long> getFriends(Long id) {
        Optional<User> user = findOne(id);
        return user.get().getFriends();
    }

    public ArrayList<User> getNotFriends(Long id) {
        return repo.getNotFriends(id);
    }

    @Override
    public Optional<User> save(User user) {
        Optional<User> saved = super.save(user);
        if(saved.isEmpty())
            notifyObservers(new UserChangeEvent(ChangeEventType.ADD, user));
        return saved;
    }

    @Override
    public Optional<User> delete(Long id) {
        Optional<User> deleted = super.delete(id);
        deleted.ifPresent(user -> notifyObservers(new UserChangeEvent(ChangeEventType.DELETE, user)));
        return deleted;
    }

    @Override
    public Optional<User> update(Long id, User user) {
        Optional<User> updated = super.update(id, user);
        if(updated.isPresent())
            notifyObservers(new UserChangeEvent(ChangeEventType.UPDATE, updated.get()));
        return updated;
    }

    @Override
    public void addObserver(Observer<UserChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<UserChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(UserChangeEvent t) {
        observers.forEach(x -> x.update(t));
    }
}
