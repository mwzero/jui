package it.jui.data;

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
     * Creates a value and returns the persisted representation. Database-backed
     * repositories may assign generated IDs here.
     */
    T create(T value);

    /**
     * Updates the value identified by {@code id} and returns the persisted value.
     */
    T update(ID id, T value);

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
