package ru.job4j.cars.repository.photo;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Photo;
import ru.job4j.cars.repository.CrudRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
@Slf4j
public class SimplePhotoRepository implements PhotoRepository {
    private final CrudRepository crudRepository;

    @Override
    public Photo create(Photo photo) {
        crudRepository.run(session -> session.persist(photo));
        return photo;
    }

    @Override
    public List<Photo> findAll() {
        return crudRepository.query("from Photo", Photo.class);
    }

    @Override
    public boolean delete(int id) {
        try {
            crudRepository.run(
                    "delete from Photo where id = :id",
                    Map.of("id", id)
            );
            return true;
        } catch (Exception e) {
            log.error("Ошибка удаления фото", e);
        }
        return false;
    }

    @Override
    public Optional<Photo> findById(int id) {
        try {
            return crudRepository.optional(session -> session.get(Photo.class, id));
        } catch (Exception e) {
            log.error("Ошибка получения фото", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Photo> findByFilePath(String filePath) {
        return crudRepository.optional(
                "from Photo where filePath = :filePath", Photo.class,
                Map.of("filePath", filePath)
        );
    }
}
