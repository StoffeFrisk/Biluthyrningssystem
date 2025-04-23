package com.example.Biluthyrningssystem.repositories;

import com.example.Biluthyrningssystem.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Date;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order,Long> {

    List<Order> findOrdersByOrderCancelledContains(boolean orderCancelled);
    List<Order> findOrdersByDateCarHiredFromBefore(Date dateCarHiredFromBefore);
}
