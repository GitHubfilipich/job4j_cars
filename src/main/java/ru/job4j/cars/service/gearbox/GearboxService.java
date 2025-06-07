package ru.job4j.cars.service.gearbox;

import ru.job4j.cars.model.Gearbox;

import java.util.Collection;
import java.util.Optional;

public interface GearboxService {
    Collection<Gearbox> findAll();

    Optional<Gearbox> findById(int id);
}
