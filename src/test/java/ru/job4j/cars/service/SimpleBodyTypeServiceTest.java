package ru.job4j.cars.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.job4j.cars.model.BodyType;
import ru.job4j.cars.repository.bodytype.BodyTypeRepository;
import ru.job4j.cars.service.bodytype.SimpleBodyTypeService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class SimpleBodyTypeServiceTest {

    private BodyTypeRepository bodyTypeRepository;
    private SimpleBodyTypeService bodyTypeService;

    @BeforeEach
    void init() {
        bodyTypeRepository = Mockito.mock(BodyTypeRepository.class);
        bodyTypeService = new SimpleBodyTypeService(bodyTypeRepository);
    }

    /**
     * Проверяет успешный сценарий поиска всех типов кузова методом {@code findAll}
     */
    @Test
    void whenFindAllThenReturnBodyTypes() {
        var bodyTypes = List.of(
                new BodyType(1, "Седан"),
                new BodyType(2, "Хэтчбек")
        );
        when(bodyTypeRepository.findAll()).thenReturn(bodyTypes);

        var actual = bodyTypeService.findAll();

        assertThat(actual).isEqualTo(bodyTypes);
        verify(bodyTypeRepository, times(1)).findAll();
    }

    /**
     * Проверяет неуспешный сценарий поиска всех типов кузова методом {@code findAll}
     */
    @Test
    void whenFindAllThenReturnEmptyList() {
        when(bodyTypeRepository.findAll()).thenReturn(List.of());

        var actual = bodyTypeService.findAll();

        assertThat(actual).isEmpty();
        verify(bodyTypeRepository, times(1)).findAll();
    }

    /**
     * Проверяет успешный сценарий поиска типа кузова по id методом {@code findById}
     */
    @Test
    void whenFindByIdThenReturnBodyType() {
        var bodyType = new BodyType(1, "Седан");
        var idCaptor = ArgumentCaptor.forClass(Integer.class);
        when(bodyTypeRepository.findById(idCaptor.capture())).thenReturn(Optional.of(bodyType));

        var actual = bodyTypeService.findById(1);

        assertThat(actual).isPresent();
        assertThat(actual.get()).isEqualTo(bodyType);
        assertThat(idCaptor.getValue()).isEqualTo(1);
        verify(bodyTypeRepository, times(1)).findById(1);
    }

    /**
     * Проверяет неуспешный сценарий поиска типа кузова по id методом {@code findById}
     */
    @Test
    void whenFindByIdNotFoundThenReturnEmpty() {
        when(bodyTypeRepository.findById(anyInt())).thenReturn(Optional.empty());

        var actual = bodyTypeService.findById(99);

        assertThat(actual).isEmpty();
        verify(bodyTypeRepository, times(1)).findById(99);
    }
}