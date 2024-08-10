package kr.co.backend.dto.Contact;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import kr.co.backend.domain.ContactComment;
import kr.co.backend.domain.User;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;


@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
@Builder
public class ContactSaveDto {

    private String title;

    private String description;

}
