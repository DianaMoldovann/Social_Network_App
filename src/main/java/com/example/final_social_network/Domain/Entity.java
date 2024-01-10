package com.example.final_social_network.Domain;

import java.io.Serializable;
import java.util.Objects;



public class Entity<ID> implements Serializable {
    /**
     * Interface for serializable entities
     */
    protected ID id;

    public Entity() {}

    public Entity(ID id) {
        this.id = id;
    }

    public void setId(ID id) {
        this.id = id;
    }

    public ID getId() { return id; }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        // if o is not the same entity as this -> false
        if (!(o instanceof Entity))
            return false;
        Entity<?> entity = (Entity<?>) o;
        // two entities are equals if their ids are equals
        return getId().equals(entity.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "Entity{" +
                "id=" + id +
                '}';
    }
}