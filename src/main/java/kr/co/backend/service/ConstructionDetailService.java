package kr.co.backend.service;

import kr.co.backend.repository.ConstructionDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ConstructionDetailService {

    private final ConstructionDetailRepository constructionDetailRepository;
}
