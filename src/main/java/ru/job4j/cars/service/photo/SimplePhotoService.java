package ru.job4j.cars.service.photo;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.job4j.cars.dto.FileDto;
import ru.job4j.cars.model.Photo;
import ru.job4j.cars.repository.photo.PhotoRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class SimplePhotoService implements PhotoService {
    private final PhotoRepository photoRepository;

    @Override
    public Optional<FileDto> findFileDtoById(int id) {
        Optional<Photo> photo = photoRepository.findById(id);
        return photo.map(this::fotoToFileDTO);
    }

    @Override
    public Optional<Photo> findPhotoById(int id) {
        return photoRepository.findById(id);
    }

    private FileDto fotoToFileDTO(Photo photo) {
        try {
            return new FileDto(Files.readAllBytes(Path.of("files", photo.getFilePath())));
        } catch (IOException e) {
            log.error("Ошибка получения фото", e);
        }
        return null;
    }

    @Override
    public boolean save(Photo photo) {
        return photoRepository.create(photo) != null;
    }

    @Override
    public Optional<Photo> findPhotoByFilePath(String filePath) {
        return photoRepository.findByFilePath(filePath);
    }

    @Override
    public Optional<FileDto> findFileDtoByFilePath(String filePath) {
        try {
                return Optional.of(new FileDto(Files.readAllBytes(Path.of("files", filePath))));
            } catch (IOException e) {
                log.error("Ошибка получения фото", e);
            }
        return Optional.empty();
    }

    @Override
    public boolean delete(int id) {
        return photoRepository.delete(id);
    }
}
