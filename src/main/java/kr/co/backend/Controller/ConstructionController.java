package kr.co.backend.Controller;

import kr.co.backend.dto.Construction.ConstructinoGetDto;
import kr.co.backend.service.ConstructionService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/construction")
@RequiredArgsConstructor
public class ConstructionController {

    private final ConstructionService constructionService;

    @GetMapping("/get-list")
    public Page<ConstructinoGetDto> getList(@PageableDefault(page = 0, size = 15, sort = "constructionId") Pageable pageable){
        return constructionService.getList(pageable);
    }

}
