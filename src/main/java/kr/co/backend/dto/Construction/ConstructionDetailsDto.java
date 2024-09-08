package kr.co.backend.dto.Construction;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class ConstructionDetailsDto {

    private String companyDetail;

    //OO호 시공사진 입니다.
    private String companyDescription;
    private String img_url;
}
