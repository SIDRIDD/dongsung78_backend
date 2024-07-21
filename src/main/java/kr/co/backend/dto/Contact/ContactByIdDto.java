package kr.co.backend.dto.Contact;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;


@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
public class ContactByIdDto {
    private Integer id;

    private String title;

    private String description;

    private String userName;

    private LocalDateTime createdAt;

    private List<CommentDto> comments;



}
