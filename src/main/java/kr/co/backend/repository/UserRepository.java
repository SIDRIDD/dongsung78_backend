package kr.co.backend.repository;

import kr.co.backend.domain.User;
import kr.co.backend.repository.custom.CustomUserRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer>, CustomUserRepository {

    Optional<User> findByEmail(String userEmail);

    boolean existsByName(String name);

    Optional<User> findByName(String userName);

    boolean existsByNameAndOauthProvider(String userName, String provider);

}
