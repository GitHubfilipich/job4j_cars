package ru.job4j.cars.repository.engine;

import ru.job4j.cars.model.Engine;

import java.util.Collection;
import java.util.Optional;

public interface EngineRepository {
    Engine create(Engine engine);

    Collection<Engine> findAll();

    void delete(int id);

    Optional<Engine> findById(int id);
}
