package kr.co.backend.service;

import kr.co.backend.domain.Role;
import kr.co.backend.domain.User;
import kr.co.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class OAuthService {

    private final UserRepository userRepository;

    public ResponseEntity<?> oauthUserSave(String userName, String provider){
        if(userRepository.existsByName(userName)){
            return ResponseEntity.badRequest().body("이미 존재하는 회원");
        }
        User user = User.builder()
                .name(userName)
                .oauthProvider(provider)
                .role(Role.CUSTOMER)
                .build();

        userRepository.save(user);
        return ResponseEntity.ok().body("Complete Oauthuser save");
    }
}
