package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import ru.job4j.cars.model.Owner;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class OwnerRepository {
    private final CrudRepository crudRepository;

    public Owner create(Owner owner) {
        crudRepository.run(session -> session.persist(owner));
        return owner;
    }

    public List<Owner> findAll() {
        return crudRepository.query("from Owner", Owner.class);
    }

    public void delete(int id) {
        crudRepository.run(
                "delete from Owner where id = :id",
                Map.of("id", id)
        );
    }
}
