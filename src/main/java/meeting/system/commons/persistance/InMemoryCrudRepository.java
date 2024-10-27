package meeting.system.commons.persistance;

import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class InMemoryCrudRepository<E, ID> implements CrudRepository<E, ID> {
    protected final Map<ID, E> entities = new HashMap<>();
    protected final Function<E, ID> idGetter;
    protected final BiConsumer<E, ID> idSetter;
    protected final Supplier<ID> idGenerator;

    @Override
    public <S extends E> S save(S entity) {
        if (geId(entity) == null)
            setId(entity, idGenerator.get());
        entities.put(geId(entity), entity);
        return entity;
    }

    @Override
    public <S extends E> Iterable<S> saveAll(Iterable<S> entities) {
        entities.forEach(this::save);
        return entities;
    }

    @Override
    public Optional<E> findById(ID id) {
        return Optional.ofNullable(entities.get(id));
    }

    @Override
    public boolean existsById(ID id) {
        return entities.containsKey(id);
    }

    @Override
    public Iterable<E> findAll() {
        return entities.values().stream().toList();
    }

    @Override
    public Iterable<E> findAllById(Iterable<ID> ids) {
        var filtered = new ArrayList<E>();
        ids.forEach(id -> {
            E entity = entities.get(id);
            if (entity != null)
                filtered.add(entity);
        });
        return filtered;
    }

    @Override
    public long count() {
        return entities.size();
    }

    @Override
    public void deleteById(ID id) {
        entities.remove(id);
    }

    @Override
    public void delete(E entity) {
        ID id = geId(entity);
        if (id == null)
            return;
        entities.remove(id);
    }

    @Override
    public void deleteAllById(Iterable<? extends ID> ids) {
        ids.forEach(this::deleteById);
    }

    @Override
    public void deleteAll(Iterable<? extends E> entities) {
        entities.forEach(this::delete);
    }

    @Override
    public void deleteAll() {
        entities.clear();
    }

    private ID geId(E entity) {
        return idGetter.apply(entity);
    }

    private void setId(E entity, ID id) {
        idSetter.accept(entity, id);
    }
}