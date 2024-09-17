package kr.co.backend.dto.Contact;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
public class ContactGetAllDto {

    private Integer id;

    private String title;

    private String description;

    private Integer userId;

    private String userName;

    private String time;

}
