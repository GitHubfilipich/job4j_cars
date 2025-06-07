package ru.job4j.cars.repository.photo;

import ru.job4j.cars.model.Photo;

import java.util.List;
import java.util.Optional;

public interface PhotoRepository {
    Photo create(Photo photo);

    List<Photo> findAll();

    boolean delete(int id);

    Optional<Photo> findById(int id);

    Optional<Photo> findByFilePath(String filePath);
}
