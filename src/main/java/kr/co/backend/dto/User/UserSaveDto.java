package kr.co.backend.dto.User;

import jakarta.persistence.PrePersist;
import kr.co.backend.domain.Role;
import kr.co.backend.domain.User;
import kr.co.backend.dto.OrderDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserSaveDto {

    private Integer userId;

    private String name;

    private AddressDto address;

    private String email;

    private String password;

    private String phoneNumber;

    private LocalDateTime createAt;

    private Role role;


    @PrePersist
    protected void onCreate() {
        if (this.createAt == null) {
            this.createAt = LocalDateTime.now();
        }
    }

    public User toEntity() {
        User user = new User();
        user.setUserId(this.userId);
        user.setName(this.name);
        user.setEmail(this.email);
        user.setPassword(this.password);
        user.setPhoneNumber(this.phoneNumber);
        user.setAddress(this.address.toEntity());
        user.setCreateAt(this.createAt);
        user.setRole(this.role);
        return user;
    }
}
