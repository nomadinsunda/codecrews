package com.example.noticeboard.security.jwt.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.noticeboard.security.jwt.domain.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
	   Optional<RefreshToken> findByToken(String token);

	   @Query(value = "SELECT p from RefreshToken p where p.keyEmail = :userEmail")
	   Optional<RefreshToken> existsByKeyEmail(@Param("userEmail") String userEmail);

	   void deleteByKeyEmail(String userEmail);
}