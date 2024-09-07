package kr.co.backend.dto.Construction;

import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import kr.co.backend.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConstructinoGetDto {

    private Integer constructionId;

    private String img_url;

    private Integer companyCode;

    private String categoryName;

    //OO대학교
    private String companyName;

    //OO 동
    private String companyDetail;

    //OO호 시공사진 입니다.
    private String companyDescription;

    private String insertDate;


    private String userName;
}
