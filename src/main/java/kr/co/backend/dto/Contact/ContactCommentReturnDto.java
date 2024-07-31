package kr.co.backend.dto.Contact;


import lombok.*;


@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
@Builder
public class ContactCommentReturnDto {
    private Integer id;

    private String username;

    private String content;

}
