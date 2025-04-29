package com.example.Biluthyrningssystem.repositories;

import com.example.Biluthyrningssystem.entities.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Orders,Long> {
    Optional<Orders> getOrdersById(long id);
}
