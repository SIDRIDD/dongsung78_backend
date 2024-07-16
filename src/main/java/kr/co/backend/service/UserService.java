package kr.co.backend.service;


import kr.co.backend.repository.UserRepository;
import kr.co.backend.dto.User.UserSaveDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public ResponseEntity<String> save(UserSaveDto userDto){

        userRepository.save(userDto.toEntity());

        return ResponseEntity.ok().body("저장되었습니다.");

    }


}
