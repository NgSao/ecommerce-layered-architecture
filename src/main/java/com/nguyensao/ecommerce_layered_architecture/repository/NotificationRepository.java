package com.nguyensao.ecommerce_layered_architecture.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nguyensao.ecommerce_layered_architecture.enums.NotificationEnum;
import com.nguyensao.ecommerce_layered_architecture.model.Notification;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserId(String userId);

    List<Notification> findByUserIdAndRead(String userId, boolean read);

    // Nếu đã đăng nhập
    @Query("SELECT n FROM Notification n WHERE n.userId = :userId OR n.type IN (:publicTypes) ORDER BY n.date DESC")
    List<Notification> findByUserIdOrPublicTypesOrderByDateDesc(@Param("userId") String userId,
            @Param("publicTypes") List<NotificationEnum> publicTypes);

    List<Notification> findByTypeInOrderByDateDesc(List<NotificationEnum> types);

    Page<Notification> findAllByOrderByIdDesc(Pageable pageable);

}