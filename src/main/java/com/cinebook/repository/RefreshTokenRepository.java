package com.cinebook.repository;

import com.cinebook.entity.RefreshToken;
import com.cinebook.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    List<RefreshToken> findAllByUser(User user);
    @Modifying
    void deleteByUser(User user);

}
