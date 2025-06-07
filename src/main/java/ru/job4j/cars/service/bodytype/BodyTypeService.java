package ru.job4j.cars.service.bodytype;

import ru.job4j.cars.model.BodyType;

import java.util.Collection;
import java.util.Optional;

public interface BodyTypeService {
    Collection<BodyType> findAll();

    Optional<BodyType> findById(int id);
}
