package kr.co.backend.service;


import com.querydsl.core.Tuple;
import jakarta.annotation.Nullable;
import kr.co.backend.repository.CategoryRepository;
import kr.co.backend.repository.ProductRepository;
import kr.co.backend.domain.Category;
import kr.co.backend.domain.Product;
import kr.co.backend.dto.Product.ProductGetDto;
import kr.co.backend.dto.ProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ResponseEntity<String> save(ProductDto productDto) {
        if (productRepository.existsByName(productDto.getName())) {
            return ResponseEntity.badRequest().body("같은 이름의 상품이 이미 등록되어 있습니다. (등록이 불가합니다.)");
        } else {
            Category category = categoryRepository.findById(productDto.getCategory()).get();
            productRepository.save(productDto.toEntity(category));
            return ResponseEntity.ok().body("상품이 저장되었습니다.");
        }

    }


    @Transactional(readOnly = true)
    public Page<ProductGetDto> getAll(Pageable pageable, @Nullable Integer categoryId) {

        Page<Tuple> results = productRepository.findFromProductGetDto(pageable, categoryId);

        List<ProductGetDto> products = results.stream().map(result -> {
            ProductGetDto productGetDto = new ProductGetDto(
                    result.get(0, Long.class),
                    result.get(1, String.class),
                    result.get(2, String.class),
                    result.get(3, Integer.class),
                    result.get(4, Integer.class),
                    result.get(5, String.class),
                    result.get(6, String.class),
                    result.get(7, Integer.class)
            );
            return productGetDto;

        }).collect(Collectors.toList());

        return new PageImpl<>(products, pageable, results.getTotalElements());


    }

    @Transactional(readOnly = true)
    public ProductGetDto getById(Long id) {

        Product product = productRepository.findById(id).get();

        ProductGetDto productGetDto = new ProductGetDto(
                product.getProductId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getImageUrl(),
                product.getCategory().getName(),
                product.getPriority()
        );

        return productGetDto;

    }
}
