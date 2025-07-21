package ru.job4j.cars.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.job4j.cars.model.Model;
import ru.job4j.cars.repository.model.ModelRepository;
import ru.job4j.cars.service.model.SimpleModelService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class SimpleModelServiceTest {

    private ModelRepository modelRepository;
    private SimpleModelService modelService;

    @BeforeEach
    void init() {
        modelRepository = Mockito.mock(ModelRepository.class);
        modelService = new SimpleModelService(modelRepository);
    }

    /**
     * Проверяет успешный сценарий поиска всех моделей методом {@code findAll}
     */
    @Test
    void whenFindAllThenReturnModels() {
        var models = List.of(
                new Model(1, "Camry"),
                new Model(2, "X5")
        );
        when(modelRepository.findAll()).thenReturn(models);

        var actual = modelService.findAll();

        assertThat(actual).isEqualTo(models);
        verify(modelRepository, times(1)).findAll();
    }

    /**
     * Проверяет неуспешный сценарий поиска всех моделей методом {@code findAll}
     */
    @Test
    void whenFindAllThenReturnEmptyList() {
        when(modelRepository.findAll()).thenReturn(List.of());

        var actual = modelService.findAll();

        assertThat(actual).isEmpty();
        verify(modelRepository, times(1)).findAll();
    }

    /**
     * Проверяет успешный сценарий поиска модели по id методом {@code findById}
     */
    @Test
    void whenFindByIdThenReturnModel() {
        var model = new Model(1, "Camry");
        var idCaptor = ArgumentCaptor.forClass(Integer.class);
        when(modelRepository.findById(idCaptor.capture())).thenReturn(Optional.of(model));

        var actual = modelService.findById(1);

        assertThat(actual).isPresent();
        assertThat(actual.get()).isEqualTo(model);
        assertThat(idCaptor.getValue()).isEqualTo(1);
        verify(modelRepository, times(1)).findById(1);
    }

    /**
     * Проверяет неуспешный сценарий поиска модели по id методом {@code findById}
     */
    @Test
    void whenFindByIdNotFoundThenReturnEmpty() {
        when(modelRepository.findById(anyInt())).thenReturn(Optional.empty());

        var actual = modelService.findById(99);

        assertThat(actual).isEmpty();
        verify(modelRepository, times(1)).findById(99);
    }
}
