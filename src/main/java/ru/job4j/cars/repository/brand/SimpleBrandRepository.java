package ru.job4j.cars.repository.brand;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.BodyType;
import ru.job4j.cars.model.Brand;
import ru.job4j.cars.repository.CrudRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
@Slf4j
public class SimpleBrandRepository implements BrandRepository {
    private final CrudRepository crudRepository;

    @Override
    public Collection<Brand> findAll() {
        try {
            return crudRepository.query("from Brand ORDER BY name", Brand.class);
        } catch (Exception e) {
            log.error("Ошибка получения марки", e);
        }
        return List.of();
    }

    @Override
    public Optional<Brand> findById(int id) {
        try {
            return crudRepository.optional(session -> session.get(Brand.class, id));
        } catch (Exception e) {
            log.error("Ошибка получения типа марки", e);
        }
        return Optional.empty();
    }
}
