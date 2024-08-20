package kr.co.backend.service;


import kr.co.backend.domain.User;
import kr.co.backend.repository.UserRepository;
import kr.co.backend.dto.User.UserSaveDto;
import lombok.RequiredArgsConstructor;
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

    public ResponseEntity<String> save(UserSaveDto userDto){
        if(userRepository.existsByName(userDto.getName())){
            return ResponseEntity.badRequest().body("이미 존재하는 회원입니다.");
        }

        User user = userDto.toEntity();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        return ResponseEntity.ok().body("저장되었습니다.");

    }

    public User findByName(String userName){
        return userRepository.findByName(userName).get();
    }




}
