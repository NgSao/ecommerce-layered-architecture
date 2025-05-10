package com.nguyensao.ecommerce_layered_architecture.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.nguyensao.ecommerce_layered_architecture.enums.StatusEnum;
import com.nguyensao.ecommerce_layered_architecture.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
        Optional<User> findByEmail(String email);

        boolean existsByEmail(String email);

        @Query("SELECT u FROM User u LEFT JOIN FETCH u.providers WHERE u.email = :email")
        Optional<User> findByEmailWithProviders(@Param("email") String email);

        Page<User> findByEmailContainingIgnoreCaseOrFullNameContainingIgnoreCase(String email, String fullName,
                        Pageable pageable);

        List<User> findAllByStatus(StatusEnum status);

        @Query("SELECT u FROM User u WHERE u.status = :status AND " +
                        "(LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                        "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                        "u.phone LIKE CONCAT('%', :keyword, '%'))")
        List<User> searchActiveUsersByKeyword(@Param("status") StatusEnum status,
                        @Param("keyword") String keyword);

        long count();
}
