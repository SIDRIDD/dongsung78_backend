package kr.co.backend.service;

import com.querydsl.core.Tuple;
import kr.co.backend.dto.Construction.ConstructionDetailsDto;
import kr.co.backend.dto.Construction.ConstructionGetDto;
import kr.co.backend.dto.Construction.ConstructionListDto;
import kr.co.backend.repository.ConstructionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ConstructionService {

    private final ConstructionRepository constructionRepository;

    @Transactional(readOnly = true)
    public Page<ConstructionListDto> getList(Pageable pageable) {

        Page<Tuple> results = constructionRepository.findListAll(pageable);

        List<ConstructionListDto> constructinoList = results.stream().map(result -> {

            ConstructionListDto constructionListDto = ConstructionListDto.builder()
                    .constructionId(result.get(0, Integer.class))
                    .companyCode(result.get(1, Integer.class))
                    .companyName(result.get(2, String.class))
                    .userName(result.get(3, String.class))
                    .categoryName(result.get(4, String.class))
                    .build();

            return constructionListDto;
        }).collect(Collectors.toList());

        long total = results.getTotalElements();

        return new PageImpl<>(constructinoList, pageable, total);

    }

    public ConstructionGetDto getOne(Integer constructionId) {
        List<Tuple> results = constructionRepository.findOne(constructionId);

        Tuple result = results.get(0);

        LocalDateTime dateTime = result.get(2, LocalDateTime.class);
        String formattedDate = null;

        try {
            formattedDate = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (NullPointerException e) {
            formattedDate = null;
        }

        String title = result.get(1, String.class)
                .concat(" ")
                .concat(result.get(4, String.class))
                .concat(" 시공 사진");

        ConstructionGetDto constructionGetDto = ConstructionGetDto.builder()
                    .constructionId(result.get(0, Integer.class))
                    .companyName(result.get(1, String.class))
                    .insertDate(formattedDate)
                    .userName(result.get(3, String.class))
                    .categoryName(result.get(4, String.class))
                    .title(title)
                    .details(getDetails(result.get(0, Integer.class)))
                    .build();

        return constructionGetDto;
    }

    private List<ConstructionDetailsDto> getDetails(Integer constructionId) {
        List<Tuple> results = constructionRepository.findDetail(constructionId);

        List<ConstructionDetailsDto> constructionGetDto = results.stream().map(result -> {
                ConstructionDetailsDto constructionDetailsDto = ConstructionDetailsDto.builder()
                .companyDetail(result.get(0, String.class))
                .companyDescription(result.get(1, String.class))
                .img_url(result.get(2, String.class))
                .build();

                return constructionDetailsDto;

        }).collect(Collectors.toList());

        return constructionGetDto;

    }
}
