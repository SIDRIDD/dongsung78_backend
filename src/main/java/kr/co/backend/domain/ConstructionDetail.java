package kr.co.backend.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Entity(name = "construction_detail_tb")
public class ConstructionDetail {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer conDetailId;

    //OO 동
    private String companyDetail;

    //OO호 시공사진 입니다.
    private String companyDescription;

    private String img_url;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "construction_id")
    private Construction construction;
}
