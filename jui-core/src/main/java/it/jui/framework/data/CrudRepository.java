package it.jui.framework.data;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Persistence boundary used by JUI's high-level CRUD component.
 * <p>
 * Implementations may be backed by memory, JDBC/H2, PostgreSQL, REST APIs, or
 * any other store. JUI only depends on this small contract.
 */
public interface CrudRepository<T, ID> {

    List<T> findAll();

    ID id(T value);

    /**
     * Creates or updates a value and returns the persisted representation. This is
     * useful for stores that assign values such as generated IDs on insert.
     */
    T save(T value);

    void deleteById(ID id);

    /**
     * Simple default suitable for small datasets. Database-backed repositories can
     * override this with an efficient keyed lookup.
     */
    default Optional<T> findById(ID id) {
        return findAll().stream()
                .filter(value -> Objects.equals(id(value), id))
                .findFirst();
    }
}
