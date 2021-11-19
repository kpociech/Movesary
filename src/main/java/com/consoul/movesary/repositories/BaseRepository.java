package com.consoul.movesary.repositories;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public interface BaseRepository <T, K> {

        T create(T entity);

        Optional<T> get(K id);

        List<T> getAll();

        T update(T entity);

        void delete(T entity);

        boolean exists(K id);
}
