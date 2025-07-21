package ru.job4j.cars.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.ResponseEntity;
import ru.job4j.cars.dto.FileDto;
import ru.job4j.cars.service.photo.PhotoService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FileControllerTest {

    private PhotoService photoService;
    private FileController fileController;

    @BeforeEach
    public void init() {
        this.photoService = mock(PhotoService.class);
        this.fileController = new FileController(photoService);
    }

    /**
     * Проверяет успешный сценарий возврата данных файла по filePath методом {@code getByFilePath}
     */
    @Test
    void whetGetByFilePathThenGetResponseEntityOk() {
        var filePath = "filePath";
        var content = new byte[]{1, 2, 3};
        var stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        when(photoService.findFileDtoByFilePath(stringArgumentCaptor.capture()))
                .thenReturn(Optional.of(new FileDto(content)));

        var expectedResponse = ResponseEntity.ok(content);

        var actualResponse = fileController.getByFilePath(filePath);
        var actualFilePath = stringArgumentCaptor.getValue();

        assertThat(actualFilePath).isEqualTo(filePath);
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    /**
     * Проверяет неуспешный сценарий возврата данных файла по filePath методом {@code getByFilePath}
     */
    @Test
    void whetGetByIncorrectFilePathThenGetResponseEntityNotFound() {
        var filePath = "filePath";
        var stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        when(photoService.findFileDtoByFilePath(stringArgumentCaptor.capture()))
                .thenReturn(Optional.empty());

        var expectedResponse = ResponseEntity.notFound().build();

        var actualResponse = fileController.getByFilePath(filePath);
        var actualFilePath = stringArgumentCaptor.getValue();

        assertThat(actualFilePath).isEqualTo(filePath);
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }
}