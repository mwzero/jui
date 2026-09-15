package it.jui.framework.data;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Small in-memory {@link CrudRepository} implementation useful for demos, tests
 * and applications that do not need durable storage.
 */
public class InMemoryCrudRepository<T, ID> implements CrudRepository<T, ID> {

    private final List<T> values;
    private final Function<T, ID> idExtractor;

    public InMemoryCrudRepository(List<T> values, Function<T, ID> idExtractor) {
        if (values == null) throw new IllegalArgumentException("values must not be null");
        if (idExtractor == null) throw new IllegalArgumentException("idExtractor must not be null");
        this.values = values;
        this.idExtractor = idExtractor;
    }

    @Override
    public List<T> findAll() {
        return List.copyOf(values);
    }

    @Override
    public ID id(T value) {
        return idExtractor.apply(value);
    }

    @Override
    public T create(T value) {
        values.add(value);
        return value;
    }

    @Override
    public T update(ID id, T value) {
        for (int i = 0; i < values.size(); i++) {
            if (Objects.equals(id(values.get(i)), id)) {
                values.set(i, value);
                return value;
            }
        }
        throw new IllegalArgumentException("No value found for id " + id);
    }

    @Override
    public void deleteById(ID id) {
        values.removeIf(value -> Objects.equals(id(value), id));
    }

    @Override
    public Optional<T> findById(ID id) {
        return values.stream()
                .filter(value -> Objects.equals(id(value), id))
                .findFirst();
    }
}
