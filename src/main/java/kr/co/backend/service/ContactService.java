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
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ContactService {

    private final ContactRepository contactRepository;

    private final ContactCommentRepository contactCommentRepository;

    private final UserRepository userRepository;

    @Value("${jwt.secret}")
    private String secretKey;

    @Transactional(readOnly = true)
    public Page<ContactGetAllDto> get(Pageable pageable) {

        Page<Tuple> results = contactRepository.getAll(pageable);

        List<ContactGetAllDto> contactGetAllDtos = results.stream().map(result -> {
            ContactGetAllDto contactGetAllDto = new ContactGetAllDto(
                    result.get(0, Integer.class),
                    result.get(1, String.class),
                    result.get(2, String.class),
                    result.get(3, Integer.class)
            );

            return contactGetAllDto;
        }).collect(Collectors.toList());

        long total = results.getTotalElements();

        return new PageImpl<>(contactGetAllDtos, pageable, total);
    }

    @Transactional(readOnly = true)
    public ContactByIdDto getById(Long id) {
        Contact contact = contactRepository.findById(id).orElseThrow(() -> new RuntimeException("Contact not found"));
        List<CommentDto> comments = contactCommentRepository.findByContactId(id).stream()
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

    public ContactCommentReturnDto addComment(Long contactId, String content, User user) {
        Contact contact = contactRepository.findById(contactId).orElseThrow(() -> new RuntimeException("Contact not found"));
        ContactComment comment = new ContactComment();
        comment.setContent(content);
        comment.setContact(contact);
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

        String jwtToken = getJwtFromCookies(request.getCookies());
        if (jwtToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        JwtUtil jwtUtil = new JwtUtil(secretKey);

        String userName = jwtUtil.getUserNameFromToken(jwtToken);
        if (userName == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        try {

            User user = userRepository.findByName(userName)
                    .orElseThrow(() -> new RuntimeException("존재 하지 않는 유저입니다."));

            Contact contact = Contact.builder()
                    .user(user)
                    .title(contactSaveDto.getTitle())
                    .description(contactSaveDto.getDescription())
                    .status(StatusContact.YET)
                    .build();

            contactRepository.save(contact);

            return ResponseEntity.ok().body("문의 글이 등록되었습니다.");
        } catch (Exception e){
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
}

