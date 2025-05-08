package com.totalelectro.dto;

import com.totalelectro.model.Product;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class ProductDTO {
    private Long id;
    private String name;
    private String short_description;
    private String long_description;
    private Integer price;
    private String dimensions;
    private CategoryDTO category;

    public ProductDTO(Product entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.short_description = entity.getShort_description();
        this.long_description = entity.getLong_description();
        this.price = entity.getPrice();
        this.dimensions = entity.getDimensions();
    }

}
