package kr.co.backend.dto.Contact;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
public class ContactGetAllDto {

    private Long id;

    private String title;

    private String description;

    private String userId;


}
