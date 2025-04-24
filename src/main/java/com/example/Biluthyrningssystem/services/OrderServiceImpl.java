package com.example.Biluthyrningssystem.services;

import com.example.Biluthyrningssystem.entities.Order;
import com.example.Biluthyrningssystem.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;

//BP
@Service
public class OrderServiceImpl implements OrderService {

    private OrderRepository orderRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository){
        this.orderRepository = orderRepository;
    }

    @Override
    public Order addOrder(Order order) {
        //Add exception checks
        return orderRepository.save(order);
    }

    @Override
    public Order cancelOrder(Order order) {
        //Add exception checks
        return orderRepository.save(order);
    }

    @Override
    public List<Order> getAllCustomerOrders() {
        return List.of();
    }

    @Override
    public List<Order> getActiveCustomerOrders() {
        return List.of();
    }

    @Override
    public List<Order> getActiveOrders() {
        return List.of();
    }

    @Override
    public List<Order> getAllOrders() {
        return List.of();
    }


    @Override
    public void deleteOrder(Order order) {
        //Add exception checks
        orderRepository.delete(order);
    }

    @Override
    public List<Long> deleteOrdersBeforeDate(Date date) {
        return List.of();
    }


}
