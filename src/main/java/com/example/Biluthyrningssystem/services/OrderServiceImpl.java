package com.example.Biluthyrningssystem.services;

import com.example.Biluthyrningssystem.entities.Orders;
import com.example.Biluthyrningssystem.exceptions.ResourceNotFoundException;
import com.example.Biluthyrningssystem.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    public List<Orders> getActiveCustomerOrders(String username) {
        return List.of();
    }

    @Override
    public List<Orders> getActiveOrders() {
        List<Orders> activeOrders = new ArrayList<>();
        for (Orders order : getAllOrders()){
            if (!order.isOrderCancelled()){
                activeOrders.add(order);
            }
        }
        return activeOrders;
    }

    @Override
    public List<Orders> getAllOrders() {
        return orderRepository.findAll();
    }


    @Override
    public void deleteOrderById(Long Id) {
        Optional<Orders> tempOrder = orderRepository.findById(Id);
        if (tempOrder.isPresent()){
            orderRepository.deleteById(Id);
        } else {
            throw new ResourceNotFoundException("Order", "ID", Id);
        }
    }

    @Override
    public List<Long> deleteOrdersBeforeDate(Date date) {
        List<Long> ordersIDsToDelete = new ArrayList<>();
        for (Orders order : getAllOrders()){
            if (order.getHireStartDate().before(date)){
                ordersIDsToDelete.add(order.getId());
                deleteOrderById(order.getId());
            }
        }
        return ordersIDsToDelete;
    }


}
