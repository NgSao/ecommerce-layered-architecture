package com.nguyensao.ecommerce_layered_architecture.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nguyensao.ecommerce_layered_architecture.enums.OrderStatus;
import com.nguyensao.ecommerce_layered_architecture.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(String userId);

    List<Order> findByUserIdAndOrderStatus(String userId, OrderStatus status);

    List<Order> findByUserIdOrderByIdDesc(String userId);

    Page<Order> findAllByOrderByIdDesc(Pageable pageable);

    long countByOrderStatus(OrderStatus status);

    @Query("SELECT MONTH(o.createdAt) as month, SUM(o.total) as totalRevenue " +
            "FROM Order o " +
            "WHERE o.orderStatus = :status AND YEAR(o.createdAt) = :year " +
            "GROUP BY MONTH(o.createdAt) " +
            "ORDER BY MONTH(o.createdAt)")
    List<Object[]> findRevenueByMonth(OrderStatus status, int year);

    @Query("SELECT COALESCE(SUM(o.total), 0) FROM Order o WHERE o.orderStatus = :status")
    double sumTotalByOrderStatus(OrderStatus status);

    long count();

    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderStatus IN (:status1)")
    long countByOrderStatusIn(OrderStatus status1);

    Optional<Order> findByOrderCode(String orderCode);

}