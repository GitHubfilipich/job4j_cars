package ru.job4j.cars.repository.gearbox;

import ru.job4j.cars.model.Gearbox;

import java.util.Collection;
import java.util.Optional;

public interface GearboxRepository {
    Collection<Gearbox> findAll();

    Optional<Gearbox> findById(int id);
}
