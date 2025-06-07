package ru.job4j.cars.dto;

import lombok.*;
import ru.job4j.cars.model.Photo;
import ru.job4j.cars.model.User;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostDTO {
    private int id;
    private int userId;
    private String userName;
    private String description;
    private int carId;
    private int bodyTypeId;
    private String bodyType;
    private int brandId;
    private String brand;
    private int modelId;
    private String model;
    private int engineId;
    private String engine;
    private int gearboxId;
    private String gearbox;
    private int productionYear;
    private int price;
    private int mileage;
    private int power;
    private boolean actual;
    private boolean used;
    private Set<String> filePaths;
    private LocalDateTime created;
}
