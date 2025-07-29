package ru.job4j.cars.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.job4j.cars.model.User;
import ru.job4j.cars.repository.user.UserRepository;
import ru.job4j.cars.service.user.SimpleUserService;
import ru.job4j.cars.service.user.UserService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class SimpleUserServiceTest {

    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void init() {
        userRepository = Mockito.mock(UserRepository.class);
        userService = new SimpleUserService(userRepository);
    }

    /**
     * Проверяет успешный сценарий сохранения пользователя методом {@code save}
     */
    @Test
    void whenSaveThenGetTrue() {
        var user = new User(1, "login", "password", "name");
        when(userRepository.create(any(User.class))).thenReturn(user);

        var result = userService.save(user);

        assertThat(result).isEqualTo(user);
    }

    /**
     * Проверяет неуспешный сценарий сохранения пользователя методом {@code save}
     */
    @Test
    void whenSaveUnsuccessfulThenGetFalse() {
        var user = new User(1, "login", "password", "name");
        when(userRepository.create(any(User.class))).thenReturn(null);

        var result = userService.save(user);

        assertThat(result).isNull();
    }

    /**
     * Проверяет успешный сценарий поиска пользователя по логину и паролю методом {@code findByLoginAndPassword}
     */
    @Test
    void whenFindByLoginAndPasswordThenGetUser() {
        var user = new User(1, "login", "password", "name");
        var stringLoginCaptor = ArgumentCaptor.forClass(String.class);
        var stringPasswordCaptor = ArgumentCaptor.forClass(String.class);
        when(userRepository.findByLoginAndPassword(stringLoginCaptor.capture(),
                stringPasswordCaptor.capture())).thenReturn(Optional.of(user));

        var result = userService.findByLoginAndPassword(user.getLogin(), user.getPassword());
        var login = stringLoginCaptor.getValue();
        var password = stringPasswordCaptor.getValue();

        assertThat(result).isPresent();
        assertThat(result.get()).usingRecursiveComparison().isEqualTo(user);
        assertThat(login).isEqualTo(user.getLogin());
        assertThat(password).isEqualTo(user.getPassword());
    }

    /**
     * Проверяет неуспешный сценарий поиска пользователя по логину и паролю методом {@code findByLoginAndPassword}
     */
    @Test
    void whenFindByLoginAndPasswordUnsuccessfulThenGetEmpty() {
        when(userRepository.findByLoginAndPassword(anyString(), anyString())).thenReturn(Optional.empty());

        var result = userService.findByLoginAndPassword("login", "password");

        assertThat(result).isEmpty();
    }

    /**
     * Проверяет успешный сценарий поиска пользователя по id методом {@code findById}
     */
    @Test
    void whenFindByIdThenGetUser() {
        var user = new User(1, "login", "password", "name");
        var intCaptor = ArgumentCaptor.forClass(Integer.class);
        when(userRepository.findById(intCaptor.capture())).thenReturn(Optional.of(user));

        var result = userService.findById(user.getId());
        var id = intCaptor.getValue();

        assertThat(result).isPresent();
        assertThat(result.get()).usingRecursiveComparison().isEqualTo(user);
        assertThat(id).isEqualTo(user.getId());
    }

    /**
     * Проверяет неуспешный сценарий поиска пользователя по id методом {@code findById}
     */
    @Test
    void whenFindByIdUnsuccessfulThenGetEmpty() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        var result = userService.findById(1);

        assertThat(result).isEmpty();
    }
}