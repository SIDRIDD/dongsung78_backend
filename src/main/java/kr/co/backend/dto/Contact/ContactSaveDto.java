package kr.co.backend.dto.Contact;


import lombok.*;



@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
@Builder
public class ContactSaveDto {

    private String title;

    private String description;

    private Integer contactType;

    private Integer typeId;

}
