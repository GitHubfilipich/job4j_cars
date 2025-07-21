package ru.job4j.cars.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.job4j.cars.model.Gearbox;
import ru.job4j.cars.repository.gearbox.GearboxRepository;
import ru.job4j.cars.service.gearbox.SimpleGearboxService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class SimpleGearboxServiceTest {

    private GearboxRepository gearboxRepository;
    private SimpleGearboxService gearboxService;

    @BeforeEach
    void init() {
        gearboxRepository = Mockito.mock(GearboxRepository.class);
        gearboxService = new SimpleGearboxService(gearboxRepository);
    }

    /**
     * Проверяет успешный сценарий поиска всех коробок передач методом {@code findAll}
     */
    @Test
    void whenFindAllThenReturnGearboxes() {
        var gearboxes = List.of(
                new Gearbox(1, "Механика"),
                new Gearbox(2, "Автомат")
        );
        when(gearboxRepository.findAll()).thenReturn(gearboxes);

        var actual = gearboxService.findAll();

        assertThat(actual).isEqualTo(gearboxes);
        verify(gearboxRepository, times(1)).findAll();
    }

    /**
     * Проверяет неуспешный сценарий поиска всех коробок передач методом {@code findAll}
     */
    @Test
    void whenFindAllThenReturnEmptyList() {
        when(gearboxRepository.findAll()).thenReturn(List.of());

        var actual = gearboxService.findAll();

        assertThat(actual).isEmpty();
        verify(gearboxRepository, times(1)).findAll();
    }

    /**
     * Проверяет успешный сценарий поиска коробки передач по id методом {@code findById}
     */
    @Test
    void whenFindByIdThenReturnGearbox() {
        var gearbox = new Gearbox(1, "Механика");
        var idCaptor = ArgumentCaptor.forClass(Integer.class);
        when(gearboxRepository.findById(idCaptor.capture())).thenReturn(Optional.of(gearbox));

        var actual = gearboxService.findById(1);

        assertThat(actual).isPresent();
        assertThat(actual.get()).isEqualTo(gearbox);
        assertThat(idCaptor.getValue()).isEqualTo(1);
        verify(gearboxRepository, times(1)).findById(1);
    }

    /**
     * Проверяет неуспешный сценарий поиска коробки передач по id методом {@code findById}
     */
    @Test
    void whenFindByIdNotFoundThenReturnEmpty() {
        when(gearboxRepository.findById(anyInt())).thenReturn(Optional.empty());

        var actual = gearboxService.findById(99);

        assertThat(actual).isEmpty();
        verify(gearboxRepository, times(1)).findById(99);
    }
}
