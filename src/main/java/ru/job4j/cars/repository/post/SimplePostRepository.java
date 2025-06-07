package ru.job4j.cars.repository.post;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.BodyType;
import ru.job4j.cars.model.Engine;
import ru.job4j.cars.model.Model;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
@Slf4j
public class SimplePostRepository implements PostRepository {
    private final CrudRepository crudRepository;

    @Override
    public Post create(Post post) {
        crudRepository.run(session -> session.persist(post));
        return post;
    }

    @Override
    public List<Post> findAll() {
        try {
            return crudRepository.query("SELECT DISTINCT p FROM Post p LEFT JOIN FETCH p.priceHistory LEFT JOIN FETCH p.participates LEFT JOIN FETCH p.photos", Post.class);
        } catch (Exception e) {
            log.error("Ошибка получения объявлений", e);
        }
        return List.of();
    }

    @Override
    public boolean delete(int id) {
        try {
            crudRepository.run(session -> {
                Post post = session.get(Post.class, id);
                if (post != null) {
                    session.delete(post);
                }
            });
            return true;
        } catch (Exception e) {
            log.error("Ошибка удаления объявления", e);
        }
        return false;
    }

    @Override
    public List<Post> findLastDayPosts() {

        return crudRepository.query("SELECT DISTINCT p FROM Post p "
                + "LEFT JOIN FETCH p.priceHistory LEFT JOIN FETCH p.participates LEFT JOIN FETCH p.photos "
                + "where p.created >= :lastDay", Post.class, Map.of("lastDay", LocalDateTime.now().minusHours(24)));
    }

    @Override
    public List<Post> findPostsWithPhoto() {
        return crudRepository.query("SELECT DISTINCT p FROM Post p "
                + "LEFT JOIN FETCH p.priceHistory LEFT JOIN FETCH p.participates INNER JOIN FETCH p.photos", Post.class);
    }

    @Override
    public List<Post> findPostsByModel(Model model) {
        return crudRepository.query("SELECT DISTINCT p FROM Post p "
                + "LEFT JOIN FETCH p.priceHistory LEFT JOIN FETCH p.participates LEFT JOIN FETCH p.photos "
                + "where p.car.model = :model", Post.class, Map.of("model", model));
    }

    @Override
    public boolean save(Post post) {
        try {
            crudRepository.run(session -> session.persist(post));
            return true;
        } catch (Exception e) {
            log.error("Ошибка сохранения объявления", e);
        }
        return false;
    }

    @Override
    public Optional<Post> findById(int id) {
        return crudRepository.optional(
                "SELECT DISTINCT p FROM Post p LEFT JOIN FETCH p.photos where p.id = :id",
                Post.class, Map.of("id", id));
    }

    @Override
    public boolean update(Post post) {
        try {
            crudRepository.run(session -> session.merge(post));
            return true;
        } catch (Exception e) {
            log.error("Ошибка обновления объявления", e);
        }
        return false;
    }
}
