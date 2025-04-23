package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import ru.job4j.cars.model.Model;
import ru.job4j.cars.model.Post;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class PostRepository {
    private final CrudRepository crudRepository;

    public Post create(Post post) {
        crudRepository.run(session -> session.persist(post));
        return post;
    }

    public List<Post> findAll() {
        return crudRepository.query("SELECT DISTINCT p FROM Post p LEFT JOIN FETCH p.priceHistory LEFT JOIN FETCH p.participates LEFT JOIN FETCH p.fotos", Post.class);
    }

    public void delete(int id) {
        crudRepository.run(
                "delete from Post where id = :id",
                Map.of("id", id)
        );
    }

    public List<Post> findLastDayPosts() {

        return crudRepository.query("SELECT DISTINCT p FROM Post p "
                + "LEFT JOIN FETCH p.priceHistory LEFT JOIN FETCH p.participates LEFT JOIN FETCH p.fotos "
                + "where p.created >= :lastDay", Post.class, Map.of("lastDay", LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0))));
    }

    public List<Post> findPostsWithFoto() {
        return crudRepository.query("SELECT DISTINCT p FROM Post p "
                + "LEFT JOIN FETCH p.priceHistory LEFT JOIN FETCH p.participates INNER JOIN FETCH p.fotos", Post.class);
    }

    public List<Post> findPostsByModel(Model model) {
        return crudRepository.query("SELECT DISTINCT p FROM Post p "
                + "LEFT JOIN FETCH p.priceHistory LEFT JOIN FETCH p.participates LEFT JOIN FETCH p.fotos "
                + "where p.car.model = :model", Post.class, Map.of("model", model));
    }
}
