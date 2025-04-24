package com.example.Biluthyrningssystem.entities;

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

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "customer_id", nullable = false)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private Customer customer;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "car_id", nullable = false)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private Car car;


    @Column(length = 10)
    private Date hireStartDate;

    @Column(length = 10)
    private Date hireEndDate;

    @Column(length = 3)
    private int numOfDaysHired;

    @Column(length = 7)
    private float totalPrice;

    @Column(nullable = true)
    private boolean orderCancelled;

    public Orders() {
    }

    public Orders(long id, Customer customer, Car car, Date hireStartDate, Date hireEndDate, int numOfDaysHired, float totalPrice, boolean orderCancelled) {
        this.id = id;
        this.customer = customer;
        this.car = car;
        this.hireStartDate = hireStartDate;
        this.hireEndDate = hireEndDate;
        this.numOfDaysHired = numOfDaysHired;
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

    public int getNumOfDaysHired() {
        return numOfDaysHired;
    }

    public void setNumOfDaysHired(int numOfDaysHired) {
        this.numOfDaysHired = numOfDaysHired;
    }

    public float getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(float totalPrice) {
        this.totalPrice = totalPrice;
    }

    public boolean isOrderCancelled() {
        return orderCancelled;
    }

    public void setOrderCancelled(boolean orderCancelled) {
        this.orderCancelled = orderCancelled;
    }
}
