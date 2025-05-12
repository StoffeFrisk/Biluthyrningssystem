package com.example.Biluthyrningssystem.services;

import com.example.Biluthyrningssystem.entities.Car;
import com.example.Biluthyrningssystem.entities.Customer;
import com.example.Biluthyrningssystem.entities.Orders;
import com.example.Biluthyrningssystem.exceptions.*;
import com.example.Biluthyrningssystem.repositories.CarRepository;
import com.example.Biluthyrningssystem.repositories.CustomerRepository;
import com.example.Biluthyrningssystem.repositories.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//BP
@Service
public class OrderServiceImpl implements OrderService {

    private final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);

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

        Optional<Customer> loggedInUser = customerRepository.findById(username);

        if (order.getCustomer() == null  && loggedInUser.isPresent()){
            LOGGER.info("Customer with ID: {} added to order.", loggedInUser.get().getPersonnummer());
            order.setCustomer(loggedInUser.get());
        } else {
            Optional<Customer> customerCheck = customerRepository.findById(order.getCustomer().getPersonnummer());
            if (customerCheck.isPresent()){
                if (!customerCheck.get().getPersonnummer().equals(username) && customerCheck.get().getPersonnummer() != null){
                    LOGGER.error("Unauthorized request by user: {}. Attempted to create order for customer with ID: {}", username, order.getCustomer().getPersonnummer());
                    throw new UnauthorisedRequestException("User", username, "create new order for user: "+order.getCustomer().getPersonnummer(), "Users can only create orders for themselves");
                }
                order.setCustomer(loggedInUser.get());
            } else {
                LOGGER.error("Customer with ID : {} not found. Request made by user: {}", order.getCustomer().getPersonnummer(), username);
                throw new ResourceNotFoundException("Customer", "ID", order.getCustomer().getPersonnummer());
            }
        }
        checkOrderInformation(order, username);
        Orders savedOrder = orderRepository.save(order);
        LOGGER.info("Order no: {}. Order placed by customer: {}", savedOrder.getId(), username);
        return savedOrder;
    }

    @Override
    public Orders cancelOrder(Orders order, String username) {
        Optional<Orders> orderCheck = orderRepository.getOrdersById(order.getId());
        if (orderCheck.isPresent()){
            order = orderCheck.get();
            if (!order.getCustomer().getPersonnummer().equals(username)){
                LOGGER.error("Unauthorized request by user: {}. Attempted to cancel order {} registered to customer with ID: {}", username,order.getId(),order.getCustomer().getPersonnummer());
                throw new UnauthorisedRequestException("User", username, "cancel an order", "Users can only edit their own orders");
            }
            if (order.isOrderCancelled()){
                LOGGER.warn("Order with ID: {} has already been cancelled. Request made by user: {}", order.getId(), username);
                throw new RepeatRequestException("Order", "ID", order.getId(), "Order has already been cancelled.");
            } else {
                order.setOrderCancelled(true);
                LOGGER.info("Order with ID {} cancelled. Request made by user: {}", order.getId(), username);
                return orderRepository.save(order);
            }

        } else {
            LOGGER.error("Order with ID: {} not found. Request made by user: {}", order.getId(), username);
            throw new ResourceNotFoundException("Order", "ID", order.getId());
        }
    }

    @Override
    public List<Orders> getAllCustomerOrders(String username) {
        LOGGER.info("Locating all orders registered to user: {} in database. Request made by user: {}", username, username);
        List<Orders> customerOrders = new ArrayList<>();
        for (Orders order: orderRepository.findAll()){
            if (order.getCustomer().getPersonnummer().equals(username)){
                customerOrders.add(order);
            }
        }
        LOGGER.info("{} order(s) found.", customerOrders.size());
        return customerOrders;
    }

    @Override
    public List<Orders> getActiveCustomerOrders(String username) {
        LOGGER.info("Locating all active orders registered to user: {} in database. Request made by user: {}", username, username);
        List<Orders> activeCustomerOrders = new ArrayList<>();
        for (Orders order: orderRepository.findAll()){
            if (!order.isOrderCancelled() && order.getCustomer().getPersonnummer().equals(username) && order.getHireEndDate().after(Date.valueOf(LocalDate.now()))){
                activeCustomerOrders.add(order);
            }
        }
        LOGGER.info("{} active order(s) found.", activeCustomerOrders.size());
        return activeCustomerOrders;
    }

    @Override
    public List<Orders> getActiveOrders() {
        LOGGER.info("Locating all active orders in database. Request made by admin");
        List<Orders> activeOrders = new ArrayList<>();
        for (Orders order : orderRepository.findAll()){
            if (!order.isOrderCancelled() && order.getHireEndDate().after(Date.valueOf(LocalDate.now()))){
                activeOrders.add(order);
            }
        }
        LOGGER.info("{} active order(s) found.", activeOrders.size());
        return activeOrders;
    }

    @Override
    public List<Orders> getAllOrders() {
        LOGGER.info("Locating all orders in database. Request made by admin");
        List<Orders> allOrders = orderRepository.findAll();
        LOGGER.info("{} order(s) found.", allOrders.size());
        return allOrders;
    }


    @Override
    public void deleteOrderById(Long Id) {
        Optional<Orders> tempOrder = orderRepository.findById(Id);
        if (tempOrder.isPresent()){
            orderRepository.deleteById(Id);
            LOGGER.info("Order with ID: {} deleted. Request made by admin", Id);
        } else {
            LOGGER.error("Order with ID: {} not found. Request made by admin", Id);
            throw new ResourceNotFoundException("Order", "ID", Id);
        }
    }

    @Override
    public List<Long> deleteOrdersBeforeDate(Date date) {
        LOGGER.info("Orders placed before {} have been requested for deletion. Request made by admin", date);
        List<Long> ordersIDsToDelete = new ArrayList<>();
        for (Orders order : orderRepository.findAll()){
            if (order.getHireStartDate().before(date)){
                ordersIDsToDelete.add(order.getId());
                deleteOrderById(order.getId());
            }
        }
        LOGGER.info("{} order(s) deleted", ordersIDsToDelete.size());
        return ordersIDsToDelete;
    }


    private void checkOrderInformation(Orders order, String username){
        if (order.getCar() == null){
            LOGGER.error("Car with ID: {} not found. Request made by {} during order creation", order.getCar(), username);
            throw new ResourceNotFoundException("Car", "ID", order.getCar());
        }
        Optional<Car> carCheck = carRepository.findById(order.getCar().getId());
        if (carCheck.isPresent()){
            LOGGER.info("Car with ID {} added to order.", carCheck.get().getId());
            order.setCar(carCheck.get());
        } else {
            LOGGER.error("Car with ID: {} not found. Request made by {} during order creation", order.getCar().getId(), username);
            throw new ResourceNotFoundException("Car", "ID", order.getCar().getId());
        }
        if (order.getHireStartDate() == null){
            LOGGER.error("Invalid date format for hireStartDate. Correct format is 'YYYY-MM-DD'. Date input by {} during order creation.", username);
            throw new IncorrectInputException("Order", "Hire Start Date", order.getHireStartDate(),"YYYY-MM-DD","Start date must be BEFORE end date.");
        }
        if (order.getHireEndDate() == null){
            LOGGER.error("Invalid date format for hireEndDate. Correct format is 'YYYY-MM-DD'. Date input by {} during order creation.", username);
            throw new IncorrectInputException("Order", "Hire End Date", order.getHireEndDate(),"YYYY-MM-DD","End date must be AfTER start date.");
        }
        if (order.getHireStartDate().before(Date.valueOf(LocalDate.now()))){
            LOGGER.error("Invalid input for hireStartDate. Start date must be set from today onwards. Date input by {} during order creation.", username);
            throw new IncorrectInputException("Order", "Hire Start Date", order.getHireStartDate(),"YYYY-MM-DD","Start date must be ON|AFTER today's date.");
        }

        if (order.getHireStartDate().after(order.getHireEndDate())){
            LOGGER.error("Invalid date(s) input. hireEndDate must come after hireStartDate. Date input by {} during order creation.", username);
            throw new IncorrectInputException("Order", "Hire Start-End Dates", ("Start:"+order.getHireStartDate()+"->End:"+order.getHireEndDate()),"YYYY-MM-DD","Start date must be BEFORE end date.");
        }
        double priceCalc = ((order.getHireEndDate().compareTo(order.getHireStartDate()))*order.getCar().getPricePerDay())+order.getCar().getPricePerDay();
        if (order.getTotalPrice() == null || order.getTotalPrice().equals(Double.valueOf(0))){
            LOGGER.info("Total price calculated to {}. Price added to order.", priceCalc);
            order.setTotalPrice(priceCalc);
        }
        if (order.getTotalPrice() != priceCalc){
            LOGGER.error("Incorrect calculation of totalPrice by user. Calculated price: {}. User input price: {}. Price input by {} during order creation.", priceCalc, order.getTotalPrice(), username);
            throw new IncorrectCalculationException("Order", "Total Price", order.getTotalPrice(), String.valueOf(priceCalc));
        }
        if (order.isOrderCancelled()){
            LOGGER.error("Invalid input of orderCancelled. Order cannot be created already cancelled. Order status input by {} during order creation.", username);
            throw new IncorrectInputException("Order", "Order Cancelled", order.isOrderCancelled(), "false","You cannot create a cancelled order.");
        } else {
            order.setOrderCancelled(false);
        }
    }

    // Niklas
    @Override
    public List<Orders> getOrdersOverlappingPeriod(LocalDate start, LocalDate end) {
        return orderRepository.findOrdersOverlappingPeriod(start, end);
    }
}
