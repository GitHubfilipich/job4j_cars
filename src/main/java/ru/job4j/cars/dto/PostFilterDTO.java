package ru.job4j.cars.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.job4j.cars.model.PostingPeriod;
import ru.job4j.cars.model.SortType;

@Getter
@Setter
@AllArgsConstructor
public class PostFilterDTO {
    Boolean used;
    Integer brandId;
    Integer modelId;
    Integer engineId;
    Integer bodyTypeId;
    Integer gearboxId;
    Integer powerMin;
    Integer powerMax;
    Integer productionYearMin;
    Integer productionYearMax;
    Integer priceMin;
    Integer priceMax;
    Boolean actual;
    Boolean withPhoto;
    SortType sortType;
    PostingPeriod postingPeriod;
}
