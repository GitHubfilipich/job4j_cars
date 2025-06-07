package ru.job4j.cars.repository.model;

import ru.job4j.cars.model.Model;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ModelRepository {
    Model create(Model model);

    Collection<Model> findAll();

    void delete(int id);

    Optional<Model> findById(int id);
}
