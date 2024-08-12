package kr.co.backend.repository.custom;

public interface CustomUserRepository {

    boolean existsByNameAndOauthProvider(String userName, String provider);
}
