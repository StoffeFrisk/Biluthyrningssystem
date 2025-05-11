// Niklas Einarsson

package com.example.Biluthyrningssystem.dto;

public class RentalDurationDTO {

    private int days;
    private long orders;

    public RentalDurationDTO(int days, long orders) {
        this.days = days;
        this.orders = orders;
    }

    public int getDays() {
        return days;
    }

    public long getOrders() {
        return orders;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public void setOrders(long orders) {
        this.orders = orders;
    }
    
}
