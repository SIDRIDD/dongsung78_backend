package kr.co.backend.service;


import com.querydsl.core.Tuple;
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

    @Transactional(readOnly = true)
    public Page<ContactGetAllDto> get(Pageable pageable) {

        Page<Tuple> results = contactRepository.getAll(pageable);

        List<ContactGetAllDto> contactGetAllDtos = results.stream().map(result -> {
            ContactGetAllDto contactGetAllDto = new ContactGetAllDto(
                    result.get(0, Long.class),
                    result.get(1, String.class),
                    result.get(2, String.class),
                    result.get(3, String.class)
            );

            return contactGetAllDto;
        }).collect(Collectors.toList());

        long total = results.getTotalElements();

        return new PageImpl<>(contactGetAllDtos, pageable, total);
    }

    @Transactional(readOnly = true)
    public ContactByIdDto getById(Long id) {
        Contact results = contactRepository.getById(id);

        ContactByIdDto contactByIdDto = new ContactByIdDto(
                results.getId(),
                results.getTitle(),
                results.getDescription(),
                results.getUser().getName(),
                results.getCreatedAt()
        );

        return contactByIdDto;

    }
}

