package ru.job4j.cars.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.job4j.cars.model.Photo;
import ru.job4j.cars.repository.photo.PhotoRepository;
import ru.job4j.cars.service.photo.SimplePhotoService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class SimplePhotoServiceTest {

    private PhotoRepository photoRepository;
    private SimplePhotoService photoService;

    @BeforeEach
    void init() {
        photoRepository = Mockito.mock(PhotoRepository.class);
        photoService = new SimplePhotoService(photoRepository);
    }

    /**
     * Проверяет успешный сценарий поиска FileDto по id методом {@code findFileDtoById}
     */
    @Test
    void whenFindFileDtoByIdThenReturnFileDto() throws IOException {
        var bytes = new byte[]{1, 2, 3};
        var tempFile = File.createTempFile("test1", ".jpg", new File("files"));
        Files.write(tempFile.toPath(), bytes);

        var photo = new Photo(1, "test1", tempFile.getName());
        when(photoRepository.findById(1)).thenReturn(Optional.of(photo));

        var actual = photoService.findFileDtoById(1);

        assertThat(actual).isPresent();
        assertThat(actual.get().getContent()).isEqualTo(bytes);

        tempFile.delete();
    }

    /**
     * Проверяет неуспешный сценарий поиска FileDto по id методом {@code findFileDtoById}
     */
    @Test
    void whenFindFileDtoByIdNotFoundThenReturnEmpty() {
        when(photoRepository.findById(anyInt())).thenReturn(Optional.empty());

        var actual = photoService.findFileDtoById(99);

        assertThat(actual).isEmpty();
    }

    /**
     * Проверяет успешный сценарий поиска Photo по id методом {@code findPhotoById}
     */
    @Test
    void whenFindPhotoByIdThenReturnPhoto() {
        var photo = new Photo(1, "test", "test.jpg");
        when(photoRepository.findById(1)).thenReturn(Optional.of(photo));

        var actual = photoService.findPhotoById(1);

        assertThat(actual).isPresent();
        assertThat(actual.get()).isEqualTo(photo);
        verify(photoRepository, times(1)).findById(1);
    }

    /**
     * Проверяет неуспешный сценарий поиска Photo по id методом {@code findPhotoById}
     */
    @Test
    void whenFindPhotoByIdNotFoundThenReturnEmpty() {
        when(photoRepository.findById(anyInt())).thenReturn(Optional.empty());

        var actual = photoService.findPhotoById(99);

        assertThat(actual).isEmpty();
        verify(photoRepository, times(1)).findById(99);
    }

    /**
     * Проверяет успешный сценарий сохранения фото методом {@code save}
     */
    @Test
    void whenSavePhotoThenReturnTrue() {
        var photo = new Photo(0, "test", "test.jpg");
        when(photoRepository.create(photo)).thenReturn(photo);

        var actual = photoService.save(photo);

        assertThat(actual).isTrue();
        verify(photoRepository, times(1)).create(photo);
    }

    /**
     * Проверяет неуспешный сценарий сохранения фото методом {@code save}
     */
    @Test
    void whenSavePhotoFailsThenReturnFalse() {
        var photo = new Photo(0, "test", "test.jpg");
        when(photoRepository.create(photo)).thenReturn(null);

        var actual = photoService.save(photo);

        assertThat(actual).isFalse();
        verify(photoRepository, times(1)).create(photo);
    }

    /**
     * Проверяет успешный сценарий поиска Photo по filePath методом {@code findPhotoByFilePath}
     */
    @Test
    void whenFindPhotoByFilePathThenReturnPhoto() {
        var photo = new Photo(1, "test", "test.jpg");
        when(photoRepository.findByFilePath("test.jpg")).thenReturn(Optional.of(photo));

        var actual = photoService.findPhotoByFilePath("test.jpg");

        assertThat(actual).isPresent();
        assertThat(actual.get()).isEqualTo(photo);
        verify(photoRepository, times(1)).findByFilePath("test.jpg");
    }

    /**
     * Проверяет неуспешный сценарий поиска Photo по filePath методом {@code findPhotoByFilePath}
     */
    @Test
    void whenFindPhotoByFilePathNotFoundThenReturnEmpty() {
        when(photoRepository.findByFilePath(anyString())).thenReturn(Optional.empty());

        var actual = photoService.findPhotoByFilePath("notfound.jpg");

        assertThat(actual).isEmpty();
        verify(photoRepository, times(1)).findByFilePath("notfound.jpg");
    }

    /**
     * Проверяет успешный сценарий поиска FileDto по filePath методом {@code findFileDtoByFilePath}
     */
    @Test
    void whenFindFileDtoByFilePathThenReturnFileDto() throws IOException {
        var bytes = new byte[]{1, 2, 3};
        var tempFile = File.createTempFile("test2", ".jpg", new File("files"));
        Files.write(tempFile.toPath(), bytes);

        var actual = photoService.findFileDtoByFilePath(tempFile.getName());

        assertThat(actual).isPresent();
        assertThat(actual.get().getContent()).isEqualTo(bytes);

        tempFile.delete();
    }

    /**
     * Проверяет неуспешный сценарий поиска FileDto по filePath методом {@code findFileDtoByFilePath}
     */
    @Test
    void whenFindFileDtoByFilePathFailsThenReturnEmpty() {
        var actual = photoService.findFileDtoByFilePath("notfound.jpg");

        assertThat(actual).isEmpty();
    }

    /**
     * Проверяет успешный сценарий удаления фото методом {@code delete}
     */
    @Test
    void whenDeletePhotoThenReturnTrue() {
        when(photoRepository.delete(1)).thenReturn(true);

        var actual = photoService.delete(1);

        assertThat(actual).isTrue();
        verify(photoRepository, times(1)).delete(1);
    }

    /**
     * Проверяет неуспешный сценарий удаления фото методом {@code delete}
     */
    @Test
    void whenDeletePhotoFailsThenReturnFalse() {
        when(photoRepository.delete(anyInt())).thenReturn(false);

        var actual = photoService.delete(99);

        assertThat(actual).isFalse();
        verify(photoRepository, times(1)).delete(99);
    }
}