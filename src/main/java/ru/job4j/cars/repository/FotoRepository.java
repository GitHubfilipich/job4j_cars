package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import ru.job4j.cars.model.Foto;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class FotoRepository {
    private final CrudRepository crudRepository;

    public Foto create(Foto foto) {
        crudRepository.run(session -> session.persist(foto));
        return foto;
    }

    public List<Foto> findAll() {
        return crudRepository.query("from Foto", Foto.class);
    }

    public void delete(int id) {
        crudRepository.run(
                "delete from Foto where id = :id",
                Map.of("id", id)
        );
    }
}
