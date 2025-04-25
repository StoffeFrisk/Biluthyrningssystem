package com.example.Biluthyrningssystem.controllers;

import com.example.Biluthyrningssystem.entities.Orders;
import com.example.Biluthyrningssystem.services.OrderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;

//BP
@Controller
@RequestMapping("api/v1")
public class OrderController {
    private OrderServiceImpl orderService;

    @Autowired
    public OrderController(OrderServiceImpl orderService){
        this.orderService = orderService;
    }

    //Customer Endpoints
    @PostMapping("/addorder")
    @ResponseBody
    public ResponseEntity<Orders> addOrder(@RequestBody Orders order){
        return new ResponseEntity<>(orderService.addOrder(order), HttpStatus.CREATED);
    }

    @PutMapping("/cancelorder")
    @ResponseBody
    public ResponseEntity<String> cancelOrder(@RequestBody Orders order){
        orderService.cancelOrder(order);
        return new ResponseEntity<>(("Orders number : " + order.getId() + " | Orders cancelled."),HttpStatus.ACCEPTED);
    }

    @GetMapping("/activeorders")
    @ResponseBody
    public ResponseEntity<List<Orders>> getActiveCustomerOrders(@AuthenticationPrincipal UserDetails userDetails){
        System.out.println(userDetails.getUsername());
        return new ResponseEntity<>(orderService.getActiveCustomerOrders(userDetails.getUsername()),HttpStatus.OK);
    }

    @GetMapping("/orders")
    @ResponseBody
    public ResponseEntity<List<Orders>> getAllCustomerOrders(){
        return new ResponseEntity<>(orderService.getAllCustomerOrders(), HttpStatus.OK);
    }

    //Admin Endpoints
    @GetMapping("/admin/activeorders")
    @ResponseBody
    public ResponseEntity<List<Orders>> getAllActiveOrders(){
        return new ResponseEntity<>(orderService.getActiveOrders(),HttpStatus.OK);
    }

    @GetMapping("/admin/orders")
    @ResponseBody
    public ResponseEntity<List<Orders>> getAllOrders(){
        return new ResponseEntity<>(orderService.getAllOrders(),HttpStatus.OK);
    }

    @DeleteMapping("/admin/removeorder")
    public ResponseEntity<String> deleteOrder(@RequestBody Orders order){
        orderService.deleteOrderById(order.getId());
        return new ResponseEntity<>(("Orders Number : " + order.getId() + " | Order removed"),HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/admin/removeorders-beforedate/{date}")
    public ResponseEntity<String> deleteOrdersBeforeDate(@PathVariable("date")Date date){
        List<Long> deletedOrders = orderService.deleteOrdersBeforeDate(date);
        return new ResponseEntity<>(("Order IDs from before "+date+" : "+deletedOrders+ " | Orders Removed"),HttpStatus.OK);
    }
}
