package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import ru.job4j.cars.model.Ownership;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class OwnershipRepository {
    private final CrudRepository crudRepository;

    public Ownership create(Ownership ownership) {
        crudRepository.run(session -> session.persist(ownership));
        return ownership;
    }

    public List<Ownership> findAll() {
        return crudRepository.query("from Ownership", Ownership.class);
    }

    public void delete(int id) {
        crudRepository.run(
                "delete from Ownership where id = :id",
                Map.of("id", id)
        );
    }
}
