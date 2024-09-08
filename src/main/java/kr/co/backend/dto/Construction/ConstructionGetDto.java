package kr.co.backend.dto.Construction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConstructionGetDto {

    private Integer constructionId;

    private String title;

    private String categoryName;

    //OO대학교
    private String companyName;

    private String insertDate;


    private String userName;

    private List<ConstructionDetailsDto> details;
}
