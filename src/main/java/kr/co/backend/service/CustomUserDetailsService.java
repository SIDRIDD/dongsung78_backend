package kr.co.backend.service;

import kr.co.backend.domain.User;
import kr.co.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@Transactional
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByName(username)
                .orElseThrow(() -> new UsernameNotFoundException("해당 이름으로 된 유저가 존재하지 않습니다.: " + username));
        String password = (user.getOauthProvider() != null) ? "" : user.getPassword();
        return new org.springframework.security.core.userdetails.User(
                user.getName(),
                password,
                new ArrayList<>()
        );
    }
}
