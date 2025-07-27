package ru.job4j.cars.service.post;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
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
import ru.job4j.cars.service.user.UserService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class SimplePostService implements PostService {
    private final PostRepository postRepository;
    private final BodyTypeService bodyTypeService;
    private final BrandService brandService;
    private final ModelService modelService;
    private final EngineService engineService;
    private final GearboxService gearboxService;
    private final PhotoService photoService;
    private final UserService userService;

    @Override
    public Collection<PostDTO> findAllByFilter(PostFilterDTO filter) {
        Collection<Post> posts = postRepository.findAll();
        posts = filter(posts, filter);
        return posts.stream().map(this::postToDTO).toList();
    }

    @Override
    public boolean save(PostDTO post, List<MultipartFile> photos) {
        return postRepository.save(postDTOToPost(post, photos));
    }

    @Override
    public boolean update(PostDTO post, List<MultipartFile> photos) {
        return postRepository.update(postDTOToPost(post, photos));
    }

    @Override
    public Optional<PostDTO> findById(int id) {
        return postRepository.findById(id)
                .map(this::postToDTO);
    }

    @Override
    public boolean delete(int id) {
        List<Photo> photos = new ArrayList<>();
        postRepository.findById(id).ifPresent(post -> photos.addAll(post.getPhotos()));
        if (postRepository.delete(id)) {
            for (Photo photo : photos) {
                try {
                    if (photoService.delete(photo.getId())) {
                        Files.deleteIfExists(Paths.get("files", photo.getFilePath()));
                    }
                } catch (IOException e) {
                    log.error("Ошибка удаления фото", e);
                }
            }
            return true;
        }
        return false;
    }

    private Collection<Post> filter(Collection<Post> posts, PostFilterDTO filter) {

        List<Predicate<Post>> predicates = new ArrayList<>();

        if (filter.getUsed() != null) {
            predicates.add(post -> post.getCar().isUsed() != filter.getUsed());
        }

        if (filter.getBrandId() != null) {
            predicates.add(post -> post.getCar().getBrand().getId() != filter.getBrandId());
        }

        if (filter.getModelId() != null) {
            predicates.add(post -> post.getCar().getModel().getId() != filter.getModelId());
        }

        if (filter.getEngineId() != null) {
            predicates.add(post -> post.getCar().getEngine().getId() != filter.getEngineId());
        }

        if (filter.getBodyTypeId() != null) {
            predicates.add(post -> post.getCar().getBodyType().getId() != filter.getBodyTypeId());
        }

        if (filter.getGearboxId() != null) {
            predicates.add(post -> post.getCar().getGearbox().getId() != filter.getGearboxId());
        }

        if (filter.getPowerMin() != null) {
            predicates.add(post -> post.getCar().getPower() < filter.getPowerMin());
        }

        if (filter.getPowerMax() != null) {
            predicates.add(post -> post.getCar().getPower() > filter.getPowerMax());
        }

        if (filter.getProductionYearMin() != null) {
            predicates.add(post -> post.getCar().getProductionYear() < filter.getProductionYearMin());
        }

        if (filter.getProductionYearMax() != null) {
            predicates.add(post -> post.getCar().getProductionYear() > filter.getProductionYearMax());
        }

        if (filter.getPriceMin() != null) {
            predicates.add(post -> post.getPrice() < filter.getPriceMin());
        }

        if (filter.getPriceMax() != null) {
            predicates.add(post -> post.getPrice() > filter.getPriceMax());
        }

        if (filter.getActual() != null && filter.getActual()) {
            predicates.add(post -> !post.isActual());
        }

        if (filter.getWithPhoto() != null && filter.getWithPhoto()) {
            predicates.add(post -> post.getPhotos().isEmpty());
        }

        if (filter.getPostingPeriod() != null) {
            LocalDateTime threshold = switch (filter.getPostingPeriod()) {
                case DAY -> LocalDateTime.now().minusDays(1);
                case WEEK -> LocalDateTime.now().minusWeeks(1);
                case MONTH -> LocalDateTime.now().minusMonths(1);
            };
            predicates.add(post -> post.getCreated().isBefore(threshold));
        }

        if (!predicates.isEmpty()) {
            Predicate<Post> predicate = predicates.stream().reduce(post -> false, Predicate::or);
            posts.removeIf(predicate);
        }

        if (filter.getSortType() != null) {
            Comparator<Post> comparator = switch (filter.getSortType()) {
                case DATE_DESC -> Comparator.comparing(Post::getCreated).reversed();
                case PRICE_ASC -> Comparator.comparingInt(Post::getPrice);
                case PRICE_DESC -> Comparator.comparingInt(Post::getPrice).reversed();
            };
            ArrayList<Post> sortedPosts = new ArrayList<>(posts);
            sortedPosts.sort(comparator);
            return sortedPosts;
        }

        return posts;
    }

    private PostDTO postToDTO(Post post) {
        return new PostDTO(post.getId(), post.getUser().getId(), post.getUser().getName(), post.getDescription(),
                post.getCar().getId(), post.getCar().getBodyType().getId(), post.getCar().getBodyType().getName(),
                post.getCar().getBrand().getId(), post.getCar().getBrand().getName(),
                post.getCar().getModel().getId(), post.getCar().getModel().getName(),
                post.getCar().getEngine().getId(), post.getCar().getEngine().getName(),
                post.getCar().getGearbox().getId(), post.getCar().getGearbox().getName(),
                post.getCar().getProductionYear(), post.getPrice(), post.getCar().getMileage(),
                post.getCar().getPower(), post.isActual(), post.getCar().isUsed(),
                post.getPhotos().stream().map(
                        Photo::getFilePath).collect(Collectors.toSet()), post.getCreated());
    }

    private Post postDTOToPost(PostDTO post, List<MultipartFile> photos) {
        Set<Photo> postPhotos = new HashSet<>();
        if (post.getFilePaths() != null) {
            post.getFilePaths().stream().map(photoService::findPhotoByFilePath)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(postPhotos::add);
        }
        if (photos != null) {
            photos.forEach(multipartFile -> {
                        if (multipartFile != null && multipartFile.getOriginalFilename() != null
                                && !multipartFile.getOriginalFilename().isBlank()) {
                            String fileName = UUID.randomUUID() + "_"
                                    + Paths.get(multipartFile.getOriginalFilename()).getFileName().toString();
                            Path filePath = Paths.get("files", fileName);
                            try {
                                Files.copy(multipartFile.getInputStream(), filePath,
                                        StandardCopyOption.REPLACE_EXISTING);
                                Photo photo = new Photo(0, multipartFile.getName(), fileName);
                                photoService.save(photo);
                                postPhotos.add(photo);
                            } catch (IOException e) {
                                log.error("Ошибка сохранения фото", e);
                            }
                        }
                    }
            );
        }
        Car car = new Car(post.getCarId(), post.getBrand() + " " + post.getModel(), engineService.findById(
                post.getEngineId()).orElse(null), null, Set.of(), modelService.findById(
                        post.getModelId()).orElse(null), bodyTypeService.findById(post.getBodyTypeId()).orElse(null),
                brandService.findById(post.getBrandId()).orElse(null), gearboxService.findById(
                        post.getGearboxId()).orElse(null), post.getProductionYear(), post.getMileage(),
                post.getPower(), post.isUsed());
        return new Post(post.getId(), post.getDescription(), LocalDateTime.now(),
                userService.findById(post.getUserId()).orElse(null), new ArrayList<>(), Set.of(), car, postPhotos,
                post.getPrice(), post.isActual());
    }
}
