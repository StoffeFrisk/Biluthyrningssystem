package com.example.Biluthyrningssystem.services;

import com.example.Biluthyrningssystem.entities.Order;

import java.sql.Date;
import java.util.List;

//BP
public interface OrderService {
//Customer
    Order addOrder(Order order);
    Order cancelOrder(Order order);
    List<Order> getAllCustomerOrders();
    List<Order> getActiveCustomerOrders();
//Admin
    List<Order> getActiveOrders();
    List<Order> getAllOrders();
    void deleteOrder(Order order);
    List<Long> deleteOrdersBeforeDate(Date date);
}
