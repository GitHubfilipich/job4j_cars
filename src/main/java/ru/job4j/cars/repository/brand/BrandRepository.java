package ru.job4j.cars.repository.brand;

import ru.job4j.cars.model.Brand;

import java.util.Collection;
import java.util.Optional;

public interface BrandRepository {
    Collection<Brand> findAll();

    Optional<Brand> findById(int id);
}
