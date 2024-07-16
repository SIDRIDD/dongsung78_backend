package kr.co.backend.Controller;


import kr.co.backend.dto.Contact.ContactByIdDto;
import kr.co.backend.dto.Contact.ContactGetAllDto;
import kr.co.backend.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @GetMapping("/getall")
    public Page<ContactGetAllDto> get(@PageableDefault(page=0, size = 15, sort = "id")Pageable pageable){
        return contactService.get(pageable);
    }

    @GetMapping("/get/{id}")
    public ContactByIdDto getById(@PathVariable("id") Long id){
        return contactService.getById(id);
    }
}
