package org.example.domain;

import java.io.Serializable;
import java.util.Objects;

public class Entity<ID> implements Serializable {
    protected ID id;

    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Entity<?> entity))
            return false;
        return getId().equals(entity.getId());
    }

    @Override
    public String toString() {
        return "{ id: " + id + " } ";
    }
}
