package org.example.service;

import org.example.domain.Entity;
import org.example.repository.Repository;
import org.example.repository.RepositoryException;

import java.util.Optional;

public abstract class Service<ID, E extends Entity<ID>> {
    private Repository<ID, E> repo;

    public Service(Repository<ID, E> repo) {
        this.repo = repo;
    }

    public Optional<E> findOne(ID id) {
        return repo.findOne(id);
    }

    public Iterable<E> findAll() {
        return repo.findAll();
    }

    /**
     * saves an entity in the repository
     *
     * @param entity the entity to be saved in the repository
     *
     * @return the saved entity, or null if the entity already exists
     *
     * @throws RepositoryException if the entity already exists in the repository
     */
    public Optional<E> save(E entity) {
        Optional<E> saved = repo.save(entity);
        if (saved.isPresent())
            throw new RepositoryException("This entity already exists!\n");
        return saved;
    }

    /**
     * deletes an entity with the specified ID from the repository
     *
     * @param id the ID of the entity to delete
     *
     * @return the deleted entity
     *
     * @throws RepositoryException if the entity with the given ID does not exist
     */
    public Optional<E> delete(ID id) {
        Optional<E> deleted = repo.delete(id);
        if (deleted.isEmpty())
            throw new RepositoryException("This entity doesn't exist!\n");
        return deleted;
    }

    /**
     * updates an entity in the repository with the specified ID
     *
     * @param id the ID of the entity to be updated (must not be null)
     * @param entity the entity to be updated (must not be null)
     * @return the updated entity if the update was successful; otherwise null
     * @throws RepositoryException if the provided entity is null or if the provided ID is null
     */
    public Optional<E> update(ID id, E entity) {
        return repo.update(id, entity);
    }
}
