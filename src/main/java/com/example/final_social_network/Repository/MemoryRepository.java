package com.example.final_social_network.Repository;

import com.example.final_social_network.Domain.Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MemoryRepository<ID, E extends Entity<ID>> implements Repository<ID,E> {
    protected Map<ID,E> entities;

    public MemoryRepository() {
        entities= new HashMap<>();
    }

    @Override
    public Optional<E> findOne(ID id){
        if (id==null)
            throw new IllegalArgumentException("ID must be not null! \n");
        return Optional.ofNullable(entities.get(id));
    }

    @Override
    public Iterable<E> getAll() {
        return entities.values();
    }

    @Override
    public Optional<E> add(E entity) {
        if (entity==null)
            throw new IllegalArgumentException("Entity must be not null!z");
        if(entities.get(entity.getId()) != null) {
            return Optional.of(entity);
        }
        else entities.put(entity.getId(),entity);
        return Optional.empty();
    }

    @Override
    public Optional<E> delete(ID id) {
        if (id == null)
            throw new IllegalArgumentException("ID must be not null!z");
        E removeEntity =  entities.get(id);
        if(removeEntity == null) {
            return Optional.empty();
        }
        else {
            entities.remove(id);
            return Optional.of(removeEntity);
        }
    }

    @Override
    public Optional<E> update(E entity) {

        if(entity == null)
            throw new IllegalArgumentException("Entity must be not null! \n");

        entities.put(entity.getId(),entity);

        if(entities.get(entity.getId()) != null) {
            entities.put(entity.getId(),entity);
            return Optional.empty();
        }
        return Optional.of(entity);
    }
}

