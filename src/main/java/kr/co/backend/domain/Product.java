package kr.co.backend.domain;

import jakarta.persistence.*;
import kr.co.backend.exception.NotEnoughStockException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "product_tb")
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    private String name;

    @Lob
    private String description;

    private int price;

    private int stock;

    private String imageUrl;

    private int priority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryId")
    private Category category;


    public Product(String name, String description, int price, int stock, String imageUrl, Category category, int priority) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.imageUrl = imageUrl;
        this.category = category;
        this.priority = priority;
    }

    public void addStock(int stock){
        this.stock += stock;
    }

    public void removeStock(int stock){
        int restStock = this.stock - stock;
        if(restStock < 0){
            throw new NotEnoughStockException("need more stock");
        }
        this.stock = restStock;
    }

    // Getters and Setters
}
