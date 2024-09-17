package kr.co.backend.service;


import kr.co.backend.domain.Address;
import kr.co.backend.domain.User;
import kr.co.backend.dto.User.AddressDto;
import kr.co.backend.dto.User.UserUpdateDto;
import kr.co.backend.repository.ContactRepository;
import kr.co.backend.repository.UserRepository;
import kr.co.backend.dto.User.UserSaveDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final ContactService contactService;

    private final OrderService orderService;

    public ResponseEntity<String> save(UserSaveDto userDto) {
        if (userRepository.existsByName(userDto.getName())) {
            return ResponseEntity.badRequest().body("이미 존재하는 회원입니다.");
        }

        User user = userDto.toEntity();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        return ResponseEntity.ok().body("저장되었습니다.");

    }

    public User findByName(String userName) {
        return userRepository.findByName(userName).get();
    }


    public ResponseEntity<?> update(UserUpdateDto userUpdateDto) {
        try {
            User user = userRepository.findById(userUpdateDto.getUserId()).orElseThrow(() -> new RuntimeException("찾을 수 없는 유저 아이디 "));

            Address address = Address.builder()
                    .roadAddress(userUpdateDto.getAddress().getRoadAddress())
                    .detailAddress(userUpdateDto.getAddress().getDetailAddress())
                    .zipcode(userUpdateDto.getAddress().getZipcode())
                    .build();

            user.setUserId(userUpdateDto.getUserId());
            user.setEmail(userUpdateDto.getEmail());
            user.setPhoneNumber(userUpdateDto.getPhoneNumber());
            user.setAddress(address);
            userRepository.save(user);
            return ResponseEntity.ok().body("수정이 완료되었습니다.");
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }


    public UserUpdateDto getUser(String userName) {
        User user = userRepository.findByName(userName)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저이름입니다."));

        AddressDto address = AddressDto.builder()
                .roadAddress(user.getAddress().getRoadAddress())
                .detailAddress(user.getAddress().getDetailAddress())
                .zipcode(user.getAddress().getZipcode())
                .build();

        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .address(address)
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .build();

        return userUpdateDto;
    }

    public ResponseEntity<?> delete(User user) {
        if(user == null){
            return ResponseEntity.badRequest().body("존재하지 않는 유저입니다.");
        } else {
            try {
                contactService.deleteByUser(user);
                userRepository.deleteById(user.getUserId());
                if(orderService.deleteOrder(user).getStatusCode() == HttpStatus.OK){
                    return ResponseEntity.ok().body("회원정보가 삭제되었습니다.");

                } else {
                    return orderService.deleteOrder(user);
                }
            } catch (Exception e){
                return ResponseEntity.internalServerError().body("삭제 중 오류가 발생하였습니다.");
            }
        }
    }
}
