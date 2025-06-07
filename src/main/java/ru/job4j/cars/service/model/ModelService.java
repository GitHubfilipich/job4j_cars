package ru.job4j.cars.service.model;

import ru.job4j.cars.model.Model;

import java.util.Collection;
import java.util.Optional;

public interface ModelService {
    Collection<Model> findAll();

    Optional<Model> findById(int id);
}
