package kr.co.backend.dto.Contact;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
public class ContactByIdDto {
    private Long id;

    private String title;

    private String description;

    private Integer userId;

    private LocalDateTime createdAt;

}
