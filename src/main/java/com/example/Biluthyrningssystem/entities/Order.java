package com.example.Biluthyrningssystem.entities;

import jakarta.persistence.*;

import java.sql.Date;

//BP
@Entity
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 7)
    private float totalPrice;

    @Column(length = 10)
    private Date hireStartDate;

    @Column(length = 10)
    private Date hireEndDate;

    @Column(length = 3)
    private int numOfDaysHired;

//    @OneToOne(cascade = CascadeType.PERSIST)
//    @JoinColumn(name = "customer_id", nullable = false)
//    @OnDelete(action = OnDeleteAction.NO_ACTION)
//    private Customer customer;

//    @OneToMany(cascade = CascadeType.PERSIST)
//    @JoinColumn(name = "car_id", nullable = false)
//    @OnDelete(action = OnDeleteAction.NO_ACTION)
//    private Car car;

    @Column(nullable = true)
    private boolean orderCancelled;

    public Order() {
    }

    public Order(long id, float totalPrice, Date hireStartDate, Date hireEndDate, int numOfDaysHired, boolean orderCancelled) {
        this.id = id;
        this.totalPrice = totalPrice;
        this.hireStartDate = hireStartDate;
        this.hireEndDate = hireEndDate;
        this.numOfDaysHired = numOfDaysHired;
        this.orderCancelled = orderCancelled;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public float getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(float totalPrice) {
        this.totalPrice = totalPrice;
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

    public boolean isOrderCancelled() {
        return orderCancelled;
    }

    public void setOrderCancelled(boolean orderCancelled) {
        this.orderCancelled = orderCancelled;
    }
}
