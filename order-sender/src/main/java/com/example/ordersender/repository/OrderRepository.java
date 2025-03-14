package com.example.ordersender.repository;

import com.example.common.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o join fetch o.products WHERE o.externalOrderId = :externalOrderId")
    Optional<Order> findByExternalOrderId(String externalOrderId);
}