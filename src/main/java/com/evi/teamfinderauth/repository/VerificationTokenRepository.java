package com.evi.teamfinderauth.repository;

import com.evi.teamfinderauth.domain.User;
import com.evi.teamfinderauth.security.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    VerificationToken findByToken(String token);

    VerificationToken findByUser(User user);

    boolean existsByUserId(Long userId);

    boolean existsByEmail(String email);

}
