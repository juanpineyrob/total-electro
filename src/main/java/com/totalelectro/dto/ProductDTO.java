package com.totalelectro.dto;

import com.totalelectro.model.Product;
import com.totalelectro.model.Category;
import lombok.Data;
import lombok.Getter;
import java.math.BigDecimal;

@Data
@Getter
public class ProductDTO {
    private Long id;
    private String name;
    private String short_description;
    private String long_description;
    private BigDecimal price;
    private String dimensions;
    private CategoryDTO category;
    private String imageUrl;

    public ProductDTO(Product entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.short_description = entity.getShort_description();
        this.long_description = entity.getLong_description();
        this.price = entity.getPrice();
        this.dimensions = entity.getDimensions();
        this.imageUrl = entity.getImageUrl();
        if (entity.getCategory() != null) {
            CategoryDTO catDto = new CategoryDTO();
            catDto.setId(entity.getCategory().getId());
            catDto.setName(entity.getCategory().getName());
            this.category = catDto;
        }
    }

}
