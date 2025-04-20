package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import ru.job4j.cars.model.Engine;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class EngineRepository {
    private final CrudRepository crudRepository;

    public Engine create(Engine engine) {
        crudRepository.run(session -> session.persist(engine));
        return engine;
    }

    public List<Engine> findAll() {
        return crudRepository.query("from Engine", Engine.class);
    }

    public void delete(int id) {
        crudRepository.run(
                "delete from Engine where id = :id",
                Map.of("id", id)
        );
    }
}
