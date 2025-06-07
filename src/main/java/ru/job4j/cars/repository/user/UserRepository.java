package ru.job4j.cars.repository.user;

import ru.job4j.cars.model.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserRepository {
    User create(User user);

    void update(User user);

    void delete(int userId);

    List<User> findAllOrderById();

    Optional<User> findById(int userId);

    List<User> findByLikeLogin(String key);

    Optional<User> findByLogin(String login);

    Optional<User> findByLoginAndPassword(String login, String password);
}
