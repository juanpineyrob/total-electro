package com.totalelectro.model;

import lombok.*;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class Product {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "short_description", nullable = false)
    private String short_description;

    @Column(name = "long_description", nullable = false)
    private String long_description;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "dimensions", nullable = false)
    private String dimensions;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToMany(mappedBy = "products")
    private Set<Order> orders = new HashSet<>();

    @Column(nullable = false)
    private Integer views = 0;

    @Column(name = "image_url")
    private String imageUrl;
}
