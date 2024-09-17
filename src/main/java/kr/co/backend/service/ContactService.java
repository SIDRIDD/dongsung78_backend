package kr.co.backend.service;


import com.querydsl.core.Tuple;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.backend.domain.ContactComment;
import kr.co.backend.domain.StatusContact;
import kr.co.backend.domain.User;
import kr.co.backend.dto.Contact.*;
import kr.co.backend.repository.ContactCommentRepository;
import kr.co.backend.repository.ContactRepository;
import kr.co.backend.domain.Contact;
import kr.co.backend.repository.UserRepository;
import kr.co.backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.hibernate.sql.ForUpdateFragment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ContactService {

    private final ContactRepository contactRepository;

    private final ContactCommentRepository contactCommentRepository;

    private final JwtUtil jwtUtil;

    private final UserRepository userRepository;

    @Value("${jwt.secret}")
    private String secretKey;

    @Transactional(readOnly = true)
    public Page<ContactGetAllDto> get(Pageable pageable) {

        Page<Tuple> results = contactRepository.getAll(pageable);

        List<ContactGetAllDto> contactGetAllDtos = results.stream().map(result -> {

            LocalDateTime dateTime = result.get(5, LocalDateTime.class);
            String formattedDate = null;

            try {
                formattedDate = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (NullPointerException e) {
                formattedDate = null;
            }

            ContactGetAllDto contactGetAllDto = new ContactGetAllDto(
                    result.get(0, Integer.class),
                    result.get(1, String.class),
                    result.get(2, String.class),
                    result.get(3, Integer.class),
                    result.get(4, String.class),
                    formattedDate
            );

            return contactGetAllDto;
        }).collect(Collectors.toList());

        long total = results.getTotalElements();

        return new PageImpl<>(contactGetAllDtos, pageable, total);
    }

    @Transactional(readOnly = true)
    public ContactByIdDto getById(Integer contactId) {

        Contact contact = contactRepository.findById(contactId).orElseThrow(() -> new RuntimeException("Contact not found"));
        List<CommentDto> comments = contactCommentRepository.findByContactId(contactId).stream()
                .map(comment -> new CommentDto(
                        comment.getId(),
                        comment.getContent(),
                        comment.getUser().getName(),
                        comment.getCreatedAt()))
                .collect(Collectors.toList());

        return new ContactByIdDto(
                contact.getId(),
                contact.getTitle(),
                contact.getDescription(),
                contact.getUser().getName(),
                contact.getCreatedAt(),
                comments
        );

    }

    public ContactCommentReturnDto addComment(Integer contactId, String content, HttpServletRequest request) {
        String userName = getUserName(request);

        User user = userRepository.findByName(userName).orElseThrow(() -> new RuntimeException("ID 가 존재하지 않습니다."));

        Contact contact = contactRepository.findById(contactId).orElseThrow(() -> new RuntimeException("Contact not found"));
        ContactComment comment = new ContactComment();
        comment.setContent(content);
        comment.setContact(contact);
        if(user.getUserId() == 1){
            comment.getContact().setStatus(StatusContact.COM);
        }
        comment.setUser(user);

        contactCommentRepository.save(comment);

        ContactCommentReturnDto contactCommentReturnDto = ContactCommentReturnDto.builder()
                .id(user.getUserId())
                .username(user.getName())
                .content(content)
                .build();


        return contactCommentReturnDto;
    }



    public ResponseEntity<String> save(ContactSaveDto contactSaveDto, HttpServletRequest request) {

        String userName = getUserName(request);

        try {

            User user = userRepository.findByName(userName)
                    .orElseThrow(() -> new RuntimeException("존재 하지 않는 유저입니다."));

            Contact contact = Contact.builder()
                    .user(user)
                    .title(contactSaveDto.getTitle())
                    .description(contactSaveDto.getDescription())
                    .contactType(contactSaveDto.getContactType())
                    .typeId(contactSaveDto.getTypeId() != null ? contactSaveDto.getTypeId() : null)
                    .status(StatusContact.YET)
                    .build();

            contactRepository.save(contact);

            return ResponseEntity.ok().body("문의 글이 등록되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("정상 등록되지 않았습니다.");
        }
    }

    private String getJwtFromCookies(Cookie[] cookies) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private String getUserName(HttpServletRequest request) {
        String jwtToken = getJwtFromCookies(request.getCookies());
        if (jwtToken == null) {
            return null;
        }

        String userName = jwtUtil.getUserNameFromToken(jwtToken);
        if (userName == null) {
            return null;
        }

        return userName;
    }

    public Page<ContactGetAllDto> getProductContact(Pageable pageable, Integer contactType, Integer typeId) {
        Page<Tuple> results = contactRepository.getProductContact(pageable, contactType, typeId);
        List<ContactGetAllDto> contactGetAllDtos = results.stream().map(result -> {

            LocalDateTime dateTime = result.get(5, LocalDateTime.class);
            String formattedDate = null;

            try {
                formattedDate = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (NullPointerException e) {
                formattedDate = null;
            }

            ContactGetAllDto contactGetAllDto = new ContactGetAllDto(
                    result.get(0, Integer.class),
                    result.get(1, String.class),
                    result.get(2, String.class),
                    result.get(3, Integer.class),
                    result.get(4, String.class),
                    formattedDate
            );

            return contactGetAllDto;
        }).collect(Collectors.toList());

        long total = results.getTotalElements();

        return new PageImpl<>(contactGetAllDtos, pageable, total);

    }

    public ResponseEntity<?> deleteById(Integer itemId) {
        if(!contactRepository.existsById(itemId)) {
            return ResponseEntity.badRequest().body("존재하지 않는 게시글 입니다.");
        }

        contactRepository.deleteById(itemId);
        return ResponseEntity.ok().body("삭제되었습니다.");
    }

    public ResponseEntity<?> deleteCommentsById(Integer commentsId){
        if(!contactCommentRepository.existsById(commentsId)){
            return ResponseEntity.badRequest().body("존재하지 않는 게시글 입니다.");
        }

        contactCommentRepository.deleteById(commentsId);
        return ResponseEntity.ok().body("삭제되었습니다.");
    }

    public ResponseEntity<?> deleteByUser(User user){

        if(contactRepository.deleteByUser(user).getStatusCode() == HttpStatus.OK){
            return ResponseEntity.ok().body(user.getUserId()+ "로 등록된 contact 가 삭제되었습니다.");

        } else {
            return ResponseEntity.internalServerError().body("삭제 중 오류가 발생하였습니다.");
        }

    }
}

