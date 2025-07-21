package ru.job4j.cars.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.User;
import ru.job4j.cars.repository.user.SimpleUserRepository;
import ru.job4j.cars.repository.user.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleUserRepositoryTest {

    private static StandardServiceRegistry registry;
    private static SessionFactory sf;
    private static UserRepository userRepository;
    private static List<User> users;

    @BeforeAll
    public static void init() {
        registry = new StandardServiceRegistryBuilder()
                .configure().build();
        sf = new MetadataSources(registry)
                .buildMetadata().buildSessionFactory();
        userRepository = new SimpleUserRepository(new CrudRepository(sf));
        users = new ArrayList<>();
        clearAllUsers();
    }

    @AfterAll
    public static void close() {
        sf.close();
        StandardServiceRegistryBuilder.destroy(registry);
    }

    @AfterEach
    public void clearData() {
        clearAllUsers();
        users.clear();
    }

    private static void clearAllUsers() {
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            session.createQuery("delete from User").executeUpdate();
            session.getTransaction().commit();
        }
    }

    private void saveUsers(List<User> usersToSave) {
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            usersToSave.forEach(session::save);
            session.getTransaction().commit();
        }
    }

    private List<User> createTestUsers() {
        return List.of(
                new User(0, "login1", "pass1", "name1"),
                new User(0, "login2", "pass2", "name2"),
                new User(0, "admin", "adminpass", "adminname")
        );
    }

    /**
     * Проверяет успешный сценарий создания пользователя методом {@code create}
     */
    @Test
    void whenCreateThenUserSaved() {
        User user = new User(0, "newlogin", "newpass", "newname");
        userRepository.create(user);

        var result = userRepository.findById(user.getId());
        assertThat(result).isPresent();
        assertThat(result.get()).usingRecursiveComparison().isEqualTo(user);
    }

    /**
     * Проверяет успешный сценарий обновления пользователя методом {@code update}
     */
    @Test
    void whenUpdateThenUserUpdated() {
        User user = new User(0, "login", "pass", "name");
        userRepository.create(user);

        user.setName("updatedName");
        userRepository.update(user);

        var result = userRepository.findById(user.getId());
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("updatedName");
    }

    /**
     * Проверяет успешный сценарий удаления пользователя методом {@code delete}
     */
    @Test
    void whenDeleteThenUserRemoved() {
        User user = new User(0, "login", "pass", "name");
        userRepository.create(user);
        int id = user.getId();

        userRepository.delete(id);

        var result = userRepository.findById(id);
        assertThat(result).isEmpty();
    }

    /**
     * Проверяет успешный сценарий возврата данных методом {@code findAllOrderById}
     */
    @Test
    void whenFindAllOrderByIdThenGetData() {
        users.addAll(createTestUsers());
        saveUsers(users);

        var result = userRepository.findAllOrderById();

        assertThat(result).usingRecursiveFieldByFieldElementComparator()
                .containsExactlyElementsOf(users);
    }

    /**
     * Проверяет неуспешный сценарий возврата данных методом {@code findAllOrderById}
     */
    @Test
    void whenFindAllOrderByIdWithoutDataThenGetEmpty() {
        var result = userRepository.findAllOrderById();
        assertThat(result).isEmpty();
    }

    /**
     * Проверяет успешный сценарий поиска пользователя по id методом {@code findById}
     */
    @Test
    void whenFindByIdThenGetData() {
        users.addAll(createTestUsers());
        saveUsers(users);
        User expected = users.get(1);

        var result = userRepository.findById(expected.getId());

        assertThat(result).isPresent();
        assertThat(result.get()).usingRecursiveComparison().isEqualTo(expected);
    }

    /**
     * Проверяет неуспешный сценарий поиска пользователя по id методом {@code findById}
     */
    @Test
    void whenFindByIdWithoutDataThenGetEmpty() {
        var result = userRepository.findById(1);
        assertThat(result).isEmpty();
    }

    /**
     * Проверяет успешный сценарий поиска пользователей по логину LIKE методом {@code findByLikeLogin}
     */
    @Test
    void whenFindByLikeLoginThenGetData() {
        users.addAll(createTestUsers());
        saveUsers(users);

        var result = userRepository.findByLikeLogin("adm");

        assertThat(result).usingRecursiveFieldByFieldElementComparator()
                .contains(users.get(2));
    }

    /**
     * Проверяет успешный сценарий поиска пользователя по логину методом {@code findByLogin}
     */
    @Test
    void whenFindByLoginThenGetData() {
        users.addAll(createTestUsers());
        saveUsers(users);
        User expected = users.get(2);

        var result = userRepository.findByLogin(expected.getLogin());

        assertThat(result).isPresent();
        assertThat(result.get()).usingRecursiveComparison().isEqualTo(expected);
    }

    /**
     * Проверяет успешный сценарий поиска пользователя по логину и паролю методом {@code findByLoginAndPassword}
     */
    @Test
    void whenFindByLoginAndPasswordThenGetData() {
        users.addAll(createTestUsers());
        saveUsers(users);
        User expected = users.get(2);

        var result = userRepository.findByLoginAndPassword(expected.getLogin(), expected.getPassword());

        assertThat(result).isPresent();
        assertThat(result.get()).usingRecursiveComparison().isEqualTo(expected);
    }
}
