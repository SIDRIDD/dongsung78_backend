package kr.co.backend.service;


import com.querydsl.core.Tuple;
import kr.co.backend.domain.ContactComment;
import kr.co.backend.domain.User;
import kr.co.backend.dto.Contact.CommentDto;
import kr.co.backend.repository.ContactCommentRepository;
import kr.co.backend.repository.ContactRepository;
import kr.co.backend.domain.Contact;
import kr.co.backend.dto.Contact.ContactByIdDto;
import kr.co.backend.dto.Contact.ContactGetAllDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ContactService {

    private final ContactRepository contactRepository;

    private final ContactCommentRepository contactCommentRepository;

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

    @Transactional
    public ContactComment addComment(Long contactId, String content, User user) {
        Contact contact = contactRepository.findById(contactId).orElseThrow(() -> new RuntimeException("Contact not found"));
        ContactComment comment = new ContactComment();
        comment.setContent(content);
        comment.setContact(contact);
        comment.setUser(user);
        return contactCommentRepository.save(comment);
    }
}

