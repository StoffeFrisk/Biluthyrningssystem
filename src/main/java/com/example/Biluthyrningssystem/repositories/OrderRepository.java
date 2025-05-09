package com.example.Biluthyrningssystem.repositories;

import com.example.Biluthyrningssystem.entities.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Orders,Long> {
    Optional<Orders> getOrdersById(long id);

    // Niklas - Query för att hitta ordrar efter datum
    @Query("SELECT o FROM Orders o WHERE o.hireStartDate <= :endDate AND o.hireEndDate >= :startDate")
    List<Orders> findOrdersOverlappingPeriod(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);




}
