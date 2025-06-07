package ru.job4j.cars.repository.bodytype;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.BodyType;
import ru.job4j.cars.repository.CrudRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
@Slf4j
public class SimpleBodyTypeRepository implements BodyTypeRepository {
    private final CrudRepository crudRepository;

    @Override
    public Collection<BodyType> findAll() {
        try {
            return crudRepository.query("from BodyType ORDER BY name", BodyType.class);
        } catch (Exception e) {
            log.error("Ошибка получения типов кузова", e);
        }
        return List.of();
    }

    @Override
    public Optional<BodyType> findById(int id) {
        try {
            return crudRepository.optional(session -> session.get(BodyType.class, id));
        } catch (Exception e) {
            log.error("Ошибка получения типа кузова", e);
        }
        return Optional.empty();
    }
}
