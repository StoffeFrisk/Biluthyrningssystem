package com.example.Biluthyrningssystem.repositories;

import com.example.Biluthyrningssystem.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order,Long> {
}
