package kr.co.backend.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "category_tb")
@Entity
public class Category {
    @Id
    private Integer categoryId;

    private String name;

    @Lob
    private String description;

    private String imgUrl;

    // Getters and Setters
}
