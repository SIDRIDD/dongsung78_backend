package kr.co.backend.dto.Contact;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CommentDto {

    private Integer id;
    private String content;
    private String username;
    private LocalDateTime createdAt;
}