// Niklas Einarsson

package com.example.Biluthyrningssystem.dto;

public class RentalDurationDTO {

    private final int days;
    private final long orders;

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
}
