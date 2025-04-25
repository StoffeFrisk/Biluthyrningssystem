package com.example.Biluthyrningssystem.services;

import com.example.Biluthyrningssystem.entities.Orders;

import java.sql.Date;
import java.util.List;

//BP
public interface OrderService {
//Customer
    Orders addOrder(Orders order);
    Orders cancelOrder(Orders order);
    List<Orders> getAllCustomerOrders();
    List<Orders> getActiveCustomerOrders(String username);
//Admin
    List<Orders> getActiveOrders();
    List<Orders> getAllOrders();
    void deleteOrderById(Long Id);
    List<Long> deleteOrdersBeforeDate(Date date);
}
