package ru.job4j.cars.repository.bodytype;

import ru.job4j.cars.model.BodyType;

import java.util.Collection;
import java.util.Optional;

public interface BodyTypeRepository {
    Collection<BodyType> findAll();

    Optional<BodyType> findById(int id);
}
