package com.example.Biluthyrningssystem.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.sql.Date;

//BP
@Entity
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "personnummer", nullable = true)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JsonIgnoreProperties("orders")
    private Customer customer;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "car_id", nullable = true)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private Car car;


    @Column(length = 10)
    private Date hireStartDate;

    @Column(length = 10)
    private Date hireEndDate;

    @Column(length = 7, nullable = true)
    private double totalPrice;

    @Column(nullable = true)
    private boolean orderCancelled;

    public Orders() {
    }

    public Orders(Customer customer, Car car, Date hireStartDate, Date hireEndDate, double totalPrice, boolean orderCancelled) {
        this.customer = customer;
        this.car = car;
        this.hireStartDate = hireStartDate;
        this.hireEndDate = hireEndDate;
        this.totalPrice = totalPrice;
        this.orderCancelled = orderCancelled;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public Date getHireStartDate() {
        return hireStartDate;
    }

    public void setHireStartDate(Date hireStartDate) {
        this.hireStartDate = hireStartDate;
    }

    public Date getHireEndDate() {
        return hireEndDate;
    }

    public void setHireEndDate(Date hireEndDate) {
        this.hireEndDate = hireEndDate;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public boolean isOrderCancelled() {
        return orderCancelled;
    }

    public void setOrderCancelled(boolean orderCancelled) {
        this.orderCancelled = orderCancelled;
    }
}
