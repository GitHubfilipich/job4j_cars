package ru.job4j.cars.service.model;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.model.Model;
import ru.job4j.cars.repository.model.ModelRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SimpleModelService implements ModelService {
    private final ModelRepository modelRepository;

    @Override
    public Collection<Model> findAll() {
        return modelRepository.findAll();
    }

    @Override
    public Optional<Model> findById(int id) {
        return modelRepository.findById(id);
    }
}
