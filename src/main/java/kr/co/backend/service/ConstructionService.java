package kr.co.backend.service;

import com.querydsl.core.Tuple;
import kr.co.backend.dto.Construction.ConstructinoGetDto;
import kr.co.backend.repository.ConstructionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ConstructionService {

    private final ConstructionRepository constructionRepository;

    public Page<ConstructinoGetDto> getList(Pageable pageable) {
        Page<Tuple> results = constructionRepository.findListAll(pageable);

        List<ConstructinoGetDto> constructinoList = results.stream().map(result -> {
            String dateTime = result.get(4, LocalDateTime.class).toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            ConstructinoGetDto constructinoGetDto = ConstructinoGetDto.builder()
                    .constructionId(result.get(0, Integer.class))
                    .companyName(result.get(1, String.class))
                    .companyDetail(result.get(2, String.class))
                    .companyDescription(result.get(3, String.class))
                    .img_url(result.get(4, String.class))
                    .insertDate(dateTime)
                    .userName(result.get(6, String.class))
                    .categoryName(result.get(7, String.class))
                    .build();

            return constructinoGetDto;
        }).collect(Collectors.toList());

        return new PageImpl<>(constructinoList, pageable, results.getTotalElements());

    }

}
