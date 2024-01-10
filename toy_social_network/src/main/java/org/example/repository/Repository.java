package org.example.repository;

import org.example.domain.Entity;
import org.example.validators.ValidationException;

import java.util.Optional;

//public interface Repository<ID, E extends Entity<ID>> {
//    E findOne(ID id);
//
//    Iterable<E> findAll();
//
//    E save(E entity);
//
//    E delete(ID id);
//
//    E update(ID id, E entity);
//}

/**
 * CRUD operations repository interface
 * @param <ID> type E must have an attribute of type ID
 * @param <E> type of entities saved in repository
 */
public interface Repository<ID, E extends Entity<ID>> {
    /**
     * @param id the id of the entity to be returned, must not be null
     * @return an {@code Optional} encapsulating the entity with the given id
     * @throws IllegalArgumentException if id is null
     */
    Optional<E> findOne(ID id);

    /**
     * @return all entities
     */
    Iterable<E> findAll();

    /**
     * @param entity must be not null
     * @return an {@code Optional} - null if the entity was saved, the entity (id already exists)
     * @throws ValidationException if the entity is not valid
     * @throws IllegalArgumentException if the given entity is null
     */
    Optional<E> save(E entity);

    /**
     * removes the entity with the specified id
     * @param id id must be not null
     * @return an {@code Optional} - null if there is no entity with the given id, the removed entity, otherwise
     * @throws IllegalArgumentException if the given id is null
     */
    Optional<E> delete(ID id);

    /**
     * @param entity must not be null
     * @return an {@code Optional} null if the entity was updated, otherwise (e.g. id does not exist) returns the entity
     * @throws IllegalArgumentException if the given entity is null
     * @throws ValidationException if the entity is not valid
     */
    Optional<E> update(ID id, E entity);
}

