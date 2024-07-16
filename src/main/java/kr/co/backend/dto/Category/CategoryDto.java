package kr.co.backend.dto.Category;


import kr.co.backend.domain.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
public class CategoryDto {

    private Integer categoryId;

    private String name;

    private String description;

    private String imgUrl;
    public Category toEntity(){
        return new Category(categoryId, name, description, imgUrl);
    }
}
