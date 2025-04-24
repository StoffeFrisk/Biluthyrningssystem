package com.example.Biluthyrningssystem.repositories;

import com.example.Biluthyrningssystem.entities.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Orders,Long> {
}
