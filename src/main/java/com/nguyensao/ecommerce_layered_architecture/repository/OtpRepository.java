package com.nguyensao.ecommerce_layered_architecture.repository;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nguyensao.ecommerce_layered_architecture.model.Otp;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {
    Optional<Otp> findByEmail(String email);

    void deleteAllByExpiresAtBefore(Instant now);

}
