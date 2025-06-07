package ru.job4j.cars.service.post;

import org.springframework.web.multipart.MultipartFile;
import ru.job4j.cars.dto.PostDTO;
import ru.job4j.cars.dto.PostFilterDTO;
import ru.job4j.cars.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PostService {
    Collection<PostDTO> findAllByFilter(PostFilterDTO filter);

    boolean save(PostDTO post, List<MultipartFile> photos);

    boolean update(PostDTO post, List<MultipartFile> photos);

    Optional<PostDTO> findById(int id);

    boolean delete(int id);
}
