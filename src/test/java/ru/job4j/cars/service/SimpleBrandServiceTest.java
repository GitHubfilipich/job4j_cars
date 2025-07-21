package ru.job4j.cars.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.job4j.cars.model.Brand;
import ru.job4j.cars.repository.brand.BrandRepository;
import ru.job4j.cars.service.brand.SimpleBrandService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class SimpleBrandServiceTest {

    private BrandRepository brandRepository;
    private SimpleBrandService brandService;

    @BeforeEach
    void init() {
        brandRepository = Mockito.mock(BrandRepository.class);
        brandService = new SimpleBrandService(brandRepository);
    }

    /**
     * Проверяет успешный сценарий поиска всех брендов методом {@code findAll}
     */
    @Test
    void whenFindAllThenReturnBrands() {
        var brands = List.of(
                new Brand(1, "Toyota"),
                new Brand(2, "BMW")
        );
        when(brandRepository.findAll()).thenReturn(brands);

        var actual = brandService.findAll();

        assertThat(actual).isEqualTo(brands);
        verify(brandRepository, times(1)).findAll();
    }

    /**
     * Проверяет неуспешный сценарий поиска всех брендов методом {@code findAll}
     */
    @Test
    void whenFindAllThenReturnEmptyList() {
        when(brandRepository.findAll()).thenReturn(List.of());

        var actual = brandService.findAll();

        assertThat(actual).isEmpty();
        verify(brandRepository, times(1)).findAll();
    }

    /**
     * Проверяет успешный сценарий поиска бренда по id методом {@code findById}
     */
    @Test
    void whenFindByIdThenReturnBrand() {
        var brand = new Brand(1, "Toyota");
        var idCaptor = ArgumentCaptor.forClass(Integer.class);
        when(brandRepository.findById(idCaptor.capture())).thenReturn(Optional.of(brand));

        var actual = brandService.findById(1);

        assertThat(actual).isPresent();
        assertThat(actual.get()).isEqualTo(brand);
        assertThat(idCaptor.getValue()).isEqualTo(1);
        verify(brandRepository, times(1)).findById(1);
    }

    /**
     * Проверяет неуспешный сценарий поиска бренда по id методом {@code findById}
     */
    @Test
    void whenFindByIdNotFoundThenReturnEmpty() {
        when(brandRepository.findById(anyInt())).thenReturn(Optional.empty());

        var actual = brandService.findById(99);

        assertThat(actual).isEmpty();
        verify(brandRepository, times(1)).findById(99);
    }
}
