package ru.job4j.cars.repository.gearbox;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.BodyType;
import ru.job4j.cars.model.Gearbox;
import ru.job4j.cars.repository.CrudRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
@Slf4j
public class SimpleGearboxRepository implements GearboxRepository {
    private final CrudRepository crudRepository;

    @Override
    public Collection<Gearbox> findAll() {
        try {
            return crudRepository.query("from Gearbox ORDER BY name", Gearbox.class);
        } catch (Exception e) {
            log.error("Ошибка получения коробки передач", e);
        }
        return List.of();
    }

    @Override
    public Optional<Gearbox> findById(int id) {
        try {
            return crudRepository.optional(session -> session.get(Gearbox.class, id));
        } catch (Exception e) {
            log.error("Ошибка получения типа коробки передач", e);
        }
        return Optional.empty();
    }
}
