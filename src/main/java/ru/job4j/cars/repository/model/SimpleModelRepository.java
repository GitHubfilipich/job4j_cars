package ru.job4j.cars.repository.model;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.BodyType;
import ru.job4j.cars.model.Engine;
import ru.job4j.cars.model.Model;
import ru.job4j.cars.repository.CrudRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
@Slf4j
public class SimpleModelRepository implements ModelRepository {
    private final CrudRepository crudRepository;

    @Override
    public Model create(Model model) {
        crudRepository.run(session -> session.persist(model));
        return model;
    }

    @Override
    public List<Model> findAll() {
        try {
            return crudRepository.query("from Model ORDER BY name", Model.class);
        } catch (Exception e) {
            log.error("Ошибка получения модели", e);
        }
        return List.of();
    }

    @Override
    public void delete(int id) {
        crudRepository.run(
                "delete from Model where id = :id",
                Map.of("id", id)
        );
    }

    @Override
    public Optional<Model> findById(int id) {
        try {
            return crudRepository.optional(session -> session.get(Model.class, id));
        } catch (Exception e) {
            log.error("Ошибка получения типа модели", e);
        }
        return Optional.empty();
    }
}
