package kr.co.backend.Controller;

import kr.co.backend.dto.Construction.ConstructionGetDto;
import kr.co.backend.dto.Construction.ConstructionListDto;
import kr.co.backend.service.ConstructionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/construction")
@RequiredArgsConstructor
public class ConstructionController {

    private final ConstructionService constructionService;

    @GetMapping("/get-list")
    public Page<ConstructionListDto> getList(@PageableDefault(page = 0, size = 10) Pageable pageable){
        return constructionService.getList(pageable);
    }

    @GetMapping("/get-one")
    public ConstructionGetDto getOne(@RequestParam("id") Integer constructionId){
        return constructionService.getOne(constructionId);
    }


}
