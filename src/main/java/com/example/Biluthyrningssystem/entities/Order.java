package com.example.Biluthyrningssystem.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
    private Date dateCarHiredFrom;

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

    public Order() {
    }

    public Order(long id, float totalPrice, Date dateCarHiredFrom, int numOfDaysHired) {
        this.id = id;
        this.totalPrice = totalPrice;
        this.dateCarHiredFrom = dateCarHiredFrom;
        this.numOfDaysHired = numOfDaysHired;
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

    public Date getDateCarHiredFrom() {
        return dateCarHiredFrom;
    }

    public void setDateCarHiredFrom(Date dateCarHiredFrom) {
        this.dateCarHiredFrom = dateCarHiredFrom;
    }

    public int getNumOfDaysHired() {
        return numOfDaysHired;
    }

    public void setNumOfDaysHired(int numOfDaysHired) {
        this.numOfDaysHired = numOfDaysHired;
    }
}
