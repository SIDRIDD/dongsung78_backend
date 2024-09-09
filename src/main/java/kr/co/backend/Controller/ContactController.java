package kr.co.backend.Controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.backend.domain.ContactComment;
import kr.co.backend.domain.User;
import kr.co.backend.dto.Contact.*;
import kr.co.backend.service.ContactService;
import kr.co.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    private final UserService userService;

    @GetMapping("/getall")
    public Page<ContactGetAllDto> get(@PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable,
                                      @RequestParam("contacttype") Integer contactType, @RequestParam(value = "typeid", required = false) Integer typeId) {
        if (contactType == 0) {
            return contactService.get(pageable);
        } else {
            return contactService.getProductContact(pageable, contactType, typeId);
        }
    }

    @GetMapping("/get")
    public ContactByIdDto getById(@RequestParam("contact_id") Integer contactId) {
        return contactService.getById(contactId);
    }

    @PostMapping("/get/{id}/comments")
    public ContactCommentReturnDto addComment(@PathVariable("id") Integer id, @RequestBody CommentRequestDto commentRequestDto, HttpServletRequest request) {
        return contactService.addComment(id, commentRequestDto.getContent(), request);
    }

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody ContactSaveDto contactSaveDto, HttpServletRequest request) {

        return contactService.save(contactSaveDto, request);

    }
}
