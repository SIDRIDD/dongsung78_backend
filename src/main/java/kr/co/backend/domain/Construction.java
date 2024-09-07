package kr.co.backend.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "construction_tb")
public class Construction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer constructionId;

    private String img_url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category categoryId;

    private Integer companyCode;

    //OO대학교
    private String companyName;

    //OO 동
    private String companyDetail;

    //OO호 시공사진 입니다.
    private String companyDescription;

    private LocalDateTime insertDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User userId;
}
