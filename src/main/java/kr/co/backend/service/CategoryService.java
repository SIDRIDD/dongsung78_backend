package kr.co.backend.service;


import com.querydsl.core.Tuple;
import kr.co.backend.repository.CategoryRepository;
import kr.co.backend.dto.Category.CategoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public ResponseEntity<String> save(CategoryDto categoryDto) {

        categoryRepository.save(categoryDto.toEntity());

        return ResponseEntity.ok().body("저장되었습니다.");

    }


    @Transactional(readOnly = true)
    public List<CategoryDto> getAll() {
        List<Tuple> results = categoryRepository.getAll();

        List<CategoryDto> categoryDtos = results.stream().map(result -> {
            CategoryDto categoryDto = new CategoryDto(
                    result.get(0, Integer.class),
                    result.get(1, String.class),
                    result.get(2, String.class),
                    result.get(3, String.class)
            );

            return categoryDto;
        }).collect(Collectors.toList());

        return categoryDtos;
    }
}
