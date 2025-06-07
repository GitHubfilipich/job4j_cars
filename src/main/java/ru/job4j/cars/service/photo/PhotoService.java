package ru.job4j.cars.service.photo;

import ru.job4j.cars.dto.FileDto;
import ru.job4j.cars.model.Photo;

import java.util.Optional;

public interface PhotoService {
    Optional<FileDto> findFileDtoById(int id);

    Optional<Photo> findPhotoById(int id);

    boolean save(Photo photo);

    Optional<Photo> findPhotoByFilePath(String s);

    Optional<FileDto> findFileDtoByFilePath(String filePath);

    boolean delete(int id);
}
