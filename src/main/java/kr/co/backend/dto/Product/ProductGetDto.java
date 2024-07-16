package kr.co.backend.dto.Product;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductGetDto {

    private Long id;

    private String name;

    private String description;

    private double price;

    private int stock;

    private String imageUrl;

    private String categoryName;

    private int priority;

}
