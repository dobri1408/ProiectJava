package service;

import java.util.List;
import java.util.Optional;

/**
 * Generic interface for CRUD operations on a repository for a specific type.
 * @param <T> - Type of the entity
 * @param <ID> - Type of the ID of the entity
 */
public interface CrudRepository<T, ID> {
    /**
     * Saves a given entity.
     * @param entity - entity to save
     * @return the saved entity
     */
    T save(T entity);

    /**
     * Retrieves an entity by its id.
     * @param id - entity id
     * @return the entity with the given id or Optional.empty() if none found
     */
    Optional<T> findById(ID id);

    /**
     * Returns all instances of the type.
     * @return all entities
     */
    List<T> findAll();

    /**
     * Updates the entity with the given id.
     * @param id - entity id
     * @param entity - updated entity
     * @return the updated entity
     */
    T update(ID id, T entity);

    /**
     * Deletes the entity with the given id.
     * @param id - entity id
     * @return true if entity was deleted, false otherwise
     */
    boolean deleteById(ID id);
}