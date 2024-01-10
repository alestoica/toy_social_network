package org.example.service;

import org.example.domain.Message;
import org.example.domain.Request;
import org.example.domain.User;
import org.example.repository.db.RequestDBPagingRepository;
import org.example.repository.db.RequestDBRepository;
import org.example.repository.paging.Page;
import org.example.repository.paging.Pageable;
import org.example.utils.events.ChangeEventType;
import org.example.utils.events.RequestChangeEvent;
import org.example.utils.events.UserChangeEvent;
import org.example.utils.observer.Observable;
import org.example.utils.observer.Observer;

import java.util.ArrayList;
import java.util.Optional;

public class RequestService extends Service<Long, Request> implements Observable<RequestChangeEvent> {
    RequestDBPagingRepository repo;
    private ArrayList<Observer<RequestChangeEvent>> observers = new ArrayList<>();

    public RequestService(RequestDBPagingRepository repo) {
        super(repo);
        this.repo = repo;
    }

    @Override
    public Optional<Request> delete(Long id) {
        Optional<Request> deleted = super.delete(id);
        deleted.ifPresent(request -> notifyObservers(new RequestChangeEvent(ChangeEventType.DELETE, request)));
        return deleted;
    }

    @Override
    public Optional<Request> update(Long id, Request request) {
        Optional<Request> updated = super.update(id, request);
        if(updated.isPresent())
            notifyObservers(new RequestChangeEvent(ChangeEventType.UPDATE, updated.get()));
        return updated;
    }

    public Page<Request> findAll(Long id, Pageable pageable) {
        return repo.findAll(id, pageable);
    }

    @Override
    public void addObserver(Observer<RequestChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<RequestChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(RequestChangeEvent t) {
        observers.forEach(x -> x.update(t));
    }
}
