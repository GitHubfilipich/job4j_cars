package ru.job4j.cars.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.job4j.cars.dto.PostDTO;
import ru.job4j.cars.dto.PostFilterDTO;
import ru.job4j.cars.model.*;
import ru.job4j.cars.repository.post.PostRepository;
import ru.job4j.cars.service.bodytype.BodyTypeService;
import ru.job4j.cars.service.brand.BrandService;
import ru.job4j.cars.service.engine.EngineService;
import ru.job4j.cars.service.gearbox.GearboxService;
import ru.job4j.cars.service.model.ModelService;
import ru.job4j.cars.service.photo.PhotoService;
import ru.job4j.cars.service.post.PostService;
import ru.job4j.cars.service.post.SimplePostService;
import ru.job4j.cars.service.user.UserService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

class SimplePostServiceTest {

    private PostRepository postRepository;
    private BodyTypeService bodyTypeService;
    private BrandService brandService;
    private ModelService modelService;
    private EngineService engineService;
    private GearboxService gearboxService;
    private PhotoService photoService;
    private UserService userService;
    private PostService postService;

    @BeforeEach
    void init() {
        postRepository = Mockito.mock(PostRepository.class);
        bodyTypeService = Mockito.mock(BodyTypeService.class);
        brandService = Mockito.mock(BrandService.class);
        modelService = Mockito.mock(ModelService.class);
        engineService = Mockito.mock(EngineService.class);
        gearboxService = Mockito.mock(GearboxService.class);
        photoService = Mockito.mock(PhotoService.class);
        userService = Mockito.mock(UserService.class);
        postService = new SimplePostService(
                postRepository,
                bodyTypeService,
                brandService,
                modelService,
                engineService,
                gearboxService,
                photoService,
                userService
        );
    }

    /**
     * Проверяет успешный сценарий поиска PostDTO методом {@code findAllByFilter}
     */
    @Test
    void whenFindAllByFilterThenGetPostDTO() {
        var posts = getPosts(3);
        when(postRepository.findAll()).thenReturn(posts);

        var filter = new PostFilterDTO(null, null, null, null, null, null,
                null, null, null, null, null, null,
                null, null, null, null);
        var dtos = posts.stream()
                .map(SimplePostServiceTest::getPostDTO)
                .toList();

        var actual = postService.findAllByFilter(filter);

        assertThat(actual).usingRecursiveFieldByFieldElementComparator().
                containsExactlyInAnyOrderElementsOf(dtos);
    }

    private static List<Post> getPosts(int count) {
        var posts = new ArrayList<Post>();
        for (int i = 0; i < count; i++) {
            var car = new Car(count, "brand" + count + " " + "model" + count, new Engine(count, "engine" + count), null, Set.of(),
                    new Model(count, "model" + count), new BodyType(count, "bodytype" + count), new Brand(count, "brand" + count),
                    new Gearbox(count, "gearbox" + count), 2020 + count, 10000 + count, 150 + count, count % 2 != 0);
            var user = new User(count, "login" + count, "password" + count, "name" + count);
            var post = new Post(count, "desc" + count, LocalDateTime.now().minusDays(count - 1), user, List.of(),
                    Set.of(), car, Set.of(), 100 + count, count % 2 != 0);
            posts.add(post);
        }
        return posts;
    }

    /**
     * Проверяет успешный сценарий поиска PostDTO методом {@code findAllByFilter}
     */
    @Test
    void whenFindAllByFilterOnlyUsedThenGetPostDTO() {
        var posts = getPosts(3);
        when(postRepository.findAll()).thenReturn(posts);

        var filter = new PostFilterDTO(true, null, null, null, null, null,
                null, null, null, null, null, null,
                null, null, null, null);
        var dtos = posts.stream()
                .map(SimplePostServiceTest::getPostDTO)
                .filter(PostDTO::isUsed)
                .toList();

        var actual = postService.findAllByFilter(filter);

        assertThat(actual).usingRecursiveFieldByFieldElementComparator().
                containsExactlyInAnyOrderElementsOf(dtos);
    }

    /**
     * Проверяет неуспешный сценарий поиска PostDTO методом {@code findAllByFilter}
     */
    @Test
    void whenFindAllByFilterWithoutDataThenGetEmpty() {
        when(postRepository.findAll()).thenReturn(List.of());

        var filter = new PostFilterDTO(null, null, null, null, null, null,
                null, null, null, null, null, null,
                null, null, null, null);

        var actual = postService.findAllByFilter(filter);

        assertThat(actual).isEmpty();
    }

    /**
     * Проверяет успешный сценарий сохранения PostDTO методом {@code save}
     */
    @Test
    void whenSaveThenGetTrue() {
        var post = getPosts(1).get(0);
        var postDTO = getPostDTO(post);

        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
        when(postRepository.save(postCaptor.capture())).thenReturn(true);

        when(bodyTypeService.findById(anyInt())).thenReturn(Optional.of(post.getCar().getBodyType()));
        when(brandService.findById(anyInt())).thenReturn(Optional.of(post.getCar().getBrand()));
        when(modelService.findById(anyInt())).thenReturn(Optional.of(post.getCar().getModel()));
        when(engineService.findById(anyInt())).thenReturn(Optional.of(post.getCar().getEngine()));
        when(gearboxService.findById(anyInt())).thenReturn(Optional.of(post.getCar().getGearbox()));
        when(userService.findById(anyInt())).thenReturn(Optional.of(post.getUser()));

        var saved = postService.save(postDTO, null);
        var savedPost = postCaptor.getValue();

        assertThat(saved).isTrue();
        assertThat(savedPost).usingRecursiveComparison()
            .withComparatorForType(Comparator.comparing(a -> a.truncatedTo(ChronoUnit.SECONDS)), LocalDateTime.class)
            .isEqualTo(post);
    }

    private static PostDTO getPostDTO(Post post) {
        return new PostDTO(post.getId(), post.getUser().getId(), post.getUser().getName(), post.getDescription(), post.getCar().getId(), post.getCar().getBodyType().getId(),
                post.getCar().getBodyType().getName(), post.getCar().getId(), post.getCar().getBrand().getName(), post.getCar().getModel().getId(), post.getCar().getModel().getName(), post.getCar().getEngine().getId(),
                post.getCar().getEngine().getName(), post.getCar().getGearbox().getId(), post.getCar().getGearbox().getName(), post.getCar().getProductionYear(), post.getPrice(), post.getCar().getMileage(),
                post.getCar().getPower(), post.isActual(), post.getCar().isUsed(), Set.of(), post.getCreated());
    }

    /**
     * Проверяет неуспешный сценарий сохранения PostDTO методом {@code save}
     */
    @Test
    void whenSaveUnsuccessfulThenGetFalse() {
        var post = getPosts(1).get(0);
        var postDTO = getPostDTO(post);

        when(postRepository.save(any(Post.class))).thenReturn(false);

        var saved = postService.save(postDTO, null);

        assertThat(saved).isFalse();
    }

    /**
     * Проверяет успешный сценарий обновления PostDTO методом {@code update}
     */
    @Test
    void whenUpdateThenGetTrue() {
        var post = getPosts(1).get(0);
        var postDTO = getPostDTO(post);

        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
        when(postRepository.update(postCaptor.capture())).thenReturn(true);

        when(bodyTypeService.findById(anyInt())).thenReturn(Optional.of(post.getCar().getBodyType()));
        when(brandService.findById(anyInt())).thenReturn(Optional.of(post.getCar().getBrand()));
        when(modelService.findById(anyInt())).thenReturn(Optional.of(post.getCar().getModel()));
        when(engineService.findById(anyInt())).thenReturn(Optional.of(post.getCar().getEngine()));
        when(gearboxService.findById(anyInt())).thenReturn(Optional.of(post.getCar().getGearbox()));
        when(userService.findById(anyInt())).thenReturn(Optional.of(post.getUser()));

        var updated = postService.update(postDTO, null);
        var updatedPost = postCaptor.getValue();

        assertThat(updated).isTrue();
        assertThat(updatedPost).usingRecursiveComparison()
                .withComparatorForType(Comparator.comparing(a -> a.truncatedTo(ChronoUnit.SECONDS)), LocalDateTime.class)
                .isEqualTo(post);
    }

    /**
     * Проверяет неуспешный сценарий обновления PostDTO методом {@code update}
     */
    @Test
    void whenUpdateUnsuccessfulThenGetFalse() {
        var post = getPosts(1).get(0);
        var postDTO = getPostDTO(post);

        when(postRepository.update(any(Post.class))).thenReturn(false);

        var updated = postService.update(postDTO, null);

        assertThat(updated).isFalse();
    }

    /**
     * Проверяет успешный сценарий поиска PostDTO по id методом {@code findById}
     */
    @Test
    void whenFindByIdThenGetPostDTO() {
        var post = getPosts(1).get(0);
        var postDTO = getPostDTO(post);

        ArgumentCaptor<Integer> intCaptor = ArgumentCaptor.forClass(Integer.class);
        when(postRepository.findById(intCaptor.capture())).thenReturn(Optional.of(post));

        var actual = postService.findById(post.getId());
        var actualId = intCaptor.getValue();

        assertThat(actual).isPresent();
        assertThat(actual.get()).usingRecursiveComparison()
                .withComparatorForType(Comparator.comparing(a -> a.truncatedTo(ChronoUnit.SECONDS)), LocalDateTime.class)
                .isEqualTo(postDTO);
        assertThat(actualId).isEqualTo(post.getId());
    }

    /**
     * Проверяет неуспешный сценарий поиска PostDTO по id методом {@code findById}
     */
    @Test
    void whenFindByIdUnsuccessfulThenGetEmpty() {
        when(postRepository.findById(anyInt())).thenReturn(Optional.empty());

        var actual = postService.findById(1);

        assertThat(actual).isEmpty();
    }

    /**
     * Проверяет успешный сценарий удаления PostDTO по id методом {@code delete}
     */
    @Test
    void whenDeleteThenGetTrue() throws IOException {
        var post = getPosts(1).get(0);

        var tempFile = File.createTempFile("test", ".jpg", new File("files"));
        Files.write(tempFile.toPath(), new byte[]{1, 2, 3});

        var photo = new Photo(2, "testFile", tempFile.getName());
        post.setPhotos(Set.of(photo));

        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));

        ArgumentCaptor<Integer> intCaptor = ArgumentCaptor.forClass(Integer.class);
        when(postRepository.delete(intCaptor.capture())).thenReturn(true);

        ArgumentCaptor<Integer> intPhotoCaptor = ArgumentCaptor.forClass(Integer.class);
        when(photoService.delete(intPhotoCaptor.capture())).thenReturn(true);

        var deleted = postService.delete(post.getId());
        var deletedId = intCaptor.getValue();
        var deletedPhotoId = intPhotoCaptor.getValue();

        assertThat(deleted).isTrue();
        assertThat(deletedId).isEqualTo(post.getId());
        assertThat(deletedPhotoId).isEqualTo(photo.getId());
        assertFalse(Files.exists(tempFile.toPath()));
    }

    /**
     * Проверяет неуспешный сценарий удаления PostDTO по id методом {@code delete}
     */
    @Test
    void whenDeleteUnsuccessfulThenGetFalse() {
        when(postRepository.delete(anyInt())).thenReturn(false);

        var deleted = postService.delete(1);

        assertThat(deleted).isFalse();
    }
}