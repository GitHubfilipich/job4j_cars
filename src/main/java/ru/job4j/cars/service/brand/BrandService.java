package ru.job4j.cars.service.brand;

import ru.job4j.cars.model.Brand;

import java.util.Collection;
import java.util.Optional;

public interface BrandService {
    Collection<Brand> findAll();

    Optional<Brand> findById(int id);
}
