package kr.co.backend.dto.Product;

import kr.co.backend.domain.Category;
import kr.co.backend.domain.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
public class ProductDto {

    private String name;

    private String description;

    private int price;

    private int stock;

    private String imageUrl;

    private Long category;

    private int priority;

    public Product toEntity(Category category){
        return new Product(name, description, price, stock, imageUrl, category, priority);
    }

}
