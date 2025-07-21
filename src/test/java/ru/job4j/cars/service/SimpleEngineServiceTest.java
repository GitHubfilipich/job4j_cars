package ru.job4j.cars.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.job4j.cars.model.Engine;
import ru.job4j.cars.repository.engine.EngineRepository;
import ru.job4j.cars.service.engine.SimpleEngineService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class SimpleEngineServiceTest {

    private EngineRepository engineRepository;
    private SimpleEngineService engineService;

    @BeforeEach
    void init() {
        engineRepository = Mockito.mock(EngineRepository.class);
        engineService = new SimpleEngineService(engineRepository);
    }

    /**
     * Проверяет успешный сценарий поиска всех двигателей методом {@code findAll}
     */
    @Test
    void whenFindAllThenReturnEngines() {
        var engines = List.of(
                new Engine(1, "Бензин"),
                new Engine(2, "Дизель")
        );
        when(engineRepository.findAll()).thenReturn(engines);

        var actual = engineService.findAll();

        assertThat(actual).isEqualTo(engines);
        verify(engineRepository, times(1)).findAll();
    }

    /**
     * Проверяет неуспешный сценарий поиска всех двигателей методом {@code findAll}
     */
    @Test
    void whenFindAllThenReturnEmptyList() {
        when(engineRepository.findAll()).thenReturn(List.of());

        var actual = engineService.findAll();

        assertThat(actual).isEmpty();
        verify(engineRepository, times(1)).findAll();
    }

    /**
     * Проверяет успешный сценарий поиска двигателя по id методом {@code findById}
     */
    @Test
    void whenFindByIdThenReturnEngine() {
        var engine = new Engine(1, "Бензин");
        var idCaptor = ArgumentCaptor.forClass(Integer.class);
        when(engineRepository.findById(idCaptor.capture())).thenReturn(Optional.of(engine));

        var actual = engineService.findById(1);

        assertThat(actual).isPresent();
        assertThat(actual.get()).isEqualTo(engine);
        assertThat(idCaptor.getValue()).isEqualTo(1);
        verify(engineRepository, times(1)).findById(1);
    }

    /**
     * Проверяет неуспешный сценарий поиска двигателя по id методом {@code findById}
     */
    @Test
    void whenFindByIdNotFoundThenReturnEmpty() {
        when(engineRepository.findById(anyInt())).thenReturn(Optional.empty());

        var actual = engineService.findById(99);

        assertThat(actual).isEmpty();
        verify(engineRepository, times(1)).findById(99);
    }
}
