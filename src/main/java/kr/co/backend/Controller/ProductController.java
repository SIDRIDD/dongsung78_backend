package kr.co.backend.Controller;


import jakarta.annotation.Nullable;
import kr.co.backend.dto.Product.ProductGetDto;
import kr.co.backend.dto.ProductDto;
import kr.co.backend.service.ProductService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product")
public class ProductController {

    private final ProductService productService;

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody ProductDto productDto){
        return productService.save(productDto);
    }

    @GetMapping("/get")
    public Page<ProductGetDto> getAll(@PageableDefault(page = 0, size = 15, sort = "priority") Pageable pageable, @RequestParam("categoryId") @Nullable Integer categoryId) {
            return productService.getAll(pageable, categoryId);
    }

    @GetMapping("/getone/{id}")
    public ProductGetDto getById(@PathVariable("id") Long id){
        return productService.getById(id);
    }

}
