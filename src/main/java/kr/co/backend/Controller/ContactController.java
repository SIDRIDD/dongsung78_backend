package kr.co.backend.Controller;


import kr.co.backend.domain.ContactComment;
import kr.co.backend.domain.User;
import kr.co.backend.dto.Contact.CommentRequestDto;
import kr.co.backend.dto.Contact.ContactByIdDto;
import kr.co.backend.dto.Contact.ContactGetAllDto;
import kr.co.backend.service.ContactService;
import kr.co.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    private final UserService userService;

    @GetMapping("/getall")
    public Page<ContactGetAllDto> get(@PageableDefault(page=0, size = 15, sort = "id")Pageable pageable){
        return contactService.get(pageable);
    }

    @GetMapping("/get/{id}")
    public ContactByIdDto getById(@PathVariable("id") Long id){
        return contactService.getById(id);
    }

    @PostMapping("/get/{id}/comments")
    public ContactComment addComment(@PathVariable("id") Long id, @RequestBody CommentRequestDto commentRequestDto) {
        User user = userService.findByName(commentRequestDto.getUserName()); // User를 가져오는 로직을 추가
        return contactService.addComment(id, commentRequestDto.getContent(), user);
    }
}
