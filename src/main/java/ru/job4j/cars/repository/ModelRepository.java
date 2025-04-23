package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import ru.job4j.cars.model.Model;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class ModelRepository {
    private final CrudRepository crudRepository;

    public Model create(Model model) {
        crudRepository.run(session -> session.persist(model));
        return model;
    }

    public List<Model> findAll() {
        return crudRepository.query("from Model", Model.class);
    }

    public void delete(int id) {
        crudRepository.run(
                "delete from Model where id = :id",
                Map.of("id", id)
        );
    }
}
