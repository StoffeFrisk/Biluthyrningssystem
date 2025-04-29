package com.example.Biluthyrningssystem.services;

import com.example.Biluthyrningssystem.entities.Car;
import com.example.Biluthyrningssystem.entities.Customer;
import com.example.Biluthyrningssystem.entities.Orders;
import com.example.Biluthyrningssystem.exceptions.IncorrectCalculationException;
import com.example.Biluthyrningssystem.exceptions.IncorrectInputException;
import com.example.Biluthyrningssystem.exceptions.UnauthorisedRequestException;
import com.example.Biluthyrningssystem.exceptions.ResourceNotFoundException;
import com.example.Biluthyrningssystem.repositories.CarRepository;
import com.example.Biluthyrningssystem.repositories.CustomerRepository;
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

    private CustomerRepository customerRepository;
    private OrderRepository orderRepository;
    private CarRepository carRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, CustomerRepository customerRepository, CarRepository carRepository){
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.carRepository = carRepository;
    }

    @Override
    public Orders addOrder(Orders order, String username) {
        Optional<Customer> customerCheck = customerRepository.findById(username);
        if (customerCheck.isPresent()){
            if (!order.getCustomer().getPersonnummer().equals(username) && order.getCustomer().getPersonnummer() != null){
                throw new UnauthorisedRequestException("User", username, "create new order for user: "+order.getCustomer().getPersonnummer(), "Users can only create orders for themselves");
            }
            order.setCustomer(customerCheck.get());
        } else {
            throw new ResourceNotFoundException("Customer", "ID", order.getCustomer().getPersonnummer());
        }


        checkOrderInformation(order);
        //Add exception checks
        return orderRepository.save(order);

    }

    @Override
    public Orders cancelOrder(Orders order, String username) {
        Optional<Orders> orderCheck = orderRepository.getOrdersById(order.getId());
        if (orderCheck.isPresent()){
            order = orderCheck.get();
            if (!order.getCustomer().getPersonnummer().equals(username)){
                throw new UnauthorisedRequestException("User", username, "cancel an order", "Users can only edit their own orders");
            }
            order.setOrderCancelled(true);
            return orderRepository.save(order);
        } else {
            throw new ResourceNotFoundException("Order", "ID", order.getId());
        }
    }

    @Override
    public List<Orders> getAllCustomerOrders(String username) {
        List<Orders> customerOrders = new ArrayList<>();
        for (Orders order: getAllOrders()){
            if (order.getCustomer().getPersonnummer().equals(username)){
                customerOrders.add(order);
            }
        }
        return customerOrders;
    }

    @Override
    public List<Orders> getActiveCustomerOrders(String username) {
        List<Orders> activeCustomerOrders = new ArrayList<>();
        for (Orders order: getActiveOrders()){
            if (order.getCustomer().getPersonnummer().equals(username)){
                activeCustomerOrders.add(order);
            }
        }
        return activeCustomerOrders;
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


    private void checkOrderInformation(Orders order){
        Optional<Car> carCheck = carRepository.findById((long) order.getCar().getId());
        if (carCheck.isPresent()){
            order.setCar(carCheck.get());
        } else {
            throw new ResourceNotFoundException("Car", "ID", order.getCar().getId());
        }
        if (order.getHireStartDate() == null){
            throw new IncorrectInputException("Order", "Hire Start Date", order.getHireStartDate(),"YYYY-MM-DD","Start date must be BEFORE end date.");
        }
        if (order.getHireEndDate() == null){
            throw new IncorrectInputException("Order", "Hire End Date", order.getHireStartDate(),"YYYY-MM-DD","End date must be AfTER end date.");
        }
        if (order.getHireStartDate().after(order.getHireEndDate())){
            throw new IncorrectInputException("Order", "Hire Start-End Dates", ("Start:"+order.getHireStartDate()+"->End:"+order.getHireEndDate()),"YYY-MM-DD","Start date must be BEFORE end date.");
        }
        double priceCalc = (order.getHireEndDate().compareTo(order.getHireStartDate()))*order.getCar().getPricePerDay();
        if ((Double)order.getTotalPrice() == null || order.getTotalPrice() == Double.valueOf(0)){
            order.setTotalPrice(priceCalc);
        }
        if (order.getTotalPrice() != priceCalc){
            throw new IncorrectCalculationException("Order", "Total Price", order.getTotalPrice(), String.valueOf(priceCalc));
        }
        if ((Boolean)order.isOrderCancelled() == null){
            order.setOrderCancelled(false);
        }
        if (order.isOrderCancelled()){
            throw new IncorrectInputException("Order", "Order Cancelled", order.isOrderCancelled(), "false","You cannot create a cancelled order.");
        }
    }
}
