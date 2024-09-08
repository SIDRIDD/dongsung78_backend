package kr.co.backend.dto.Construction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class ConstructionListDto {
    private Integer constructionId;

    private Integer companyCode;

    private String companyName;

    private String userName;

    private String categoryName;
}
