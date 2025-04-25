package com.example.Biluthyrningssystem.services;

import com.example.Biluthyrningssystem.entities.Orders;
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
    public Orders addOrder(Orders order) {
        //Add exception checks
        return orderRepository.save(order);
    }

    @Override
    public Orders cancelOrder(Orders order) {
        //Add exception checks
        return orderRepository.save(order);
    }

    @Override
    public List<Orders> getAllCustomerOrders() {
        return List.of();
    }

    @Override
    public List<Orders> getActiveCustomerOrders() {
        return List.of();
    }

    @Override
    public List<Orders> getActiveOrders() {
        return List.of();
    }

    @Override
    public List<Orders> getAllOrders() {
        return List.of();
    }


    @Override
    public void deleteOrder(Orders order) {
        //Add exception checks
        orderRepository.delete(order);
    }

    @Override
    public List<Long> deleteOrdersBeforeDate(Date date) {
        return List.of();
    }


}
