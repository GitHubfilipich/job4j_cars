package ru.job4j.cars.repository.engine;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.BodyType;
import ru.job4j.cars.model.Engine;
import ru.job4j.cars.repository.CrudRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
@Slf4j
public class SimpleEngineRepository implements EngineRepository {
    private final CrudRepository crudRepository;

    @Override
    public Engine create(Engine engine) {
        crudRepository.run(session -> session.persist(engine));
        return engine;
    }

    @Override
    public List<Engine> findAll() {
        try {
            return crudRepository.query("from Engine ORDER BY name", Engine.class);
        } catch (Exception e) {
            log.error("Ошибка получения двигателя", e);
        }
        return List.of();
    }

    @Override
    public void delete(int id) {
        crudRepository.run(
                "delete from Engine where id = :id",
                Map.of("id", id)
        );
    }

    @Override
    public Optional<Engine> findById(int id) {
        try {
            return crudRepository.optional(session -> session.get(Engine.class, id));
        } catch (Exception e) {
            log.error("Ошибка получения типа двигателя", e);
        }
        return Optional.empty();
    }
}
