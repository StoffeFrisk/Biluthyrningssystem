package com.example.Biluthyrningssystem.entities;

import jakarta.persistence.*;

@Entity
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String street;

    @Column(length = 10, nullable = false)
    private String postalCode;

    @Column(length = 50, nullable = false)
    private String city;

    public Address() {
    }

    public Address(String street, String postalCode, String city) {
        this.street = street;
        this.postalCode = postalCode;
        this.city = city;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
    @Override
    public String toString() {
        return " [id=" + id + ", street=" + street + ", postalCode=" + postalCode + ", city=" + city + "]";
    }
}
