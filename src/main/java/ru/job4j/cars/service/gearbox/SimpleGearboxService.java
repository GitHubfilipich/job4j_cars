package ru.job4j.cars.service.gearbox;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.model.Gearbox;
import ru.job4j.cars.repository.gearbox.GearboxRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SimpleGearboxService implements GearboxService {
    private final GearboxRepository gearboxRepository;

    @Override
    public Collection<Gearbox> findAll() {
        return gearboxRepository.findAll();
    }

    @Override
    public Optional<Gearbox> findById(int id) {
        return gearboxRepository.findById(id);
    }
}
