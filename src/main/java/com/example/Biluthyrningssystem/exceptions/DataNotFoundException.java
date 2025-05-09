// Niklas Einarsson

package com.example.Biluthyrningssystem.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class DataNotFoundException extends RuntimeException{

    private String context;
    private String period;
    private String message;


    public DataNotFoundException(String context, String period, String message) {
        super(String.format("%s: '%s %s'", context, message, period));
        this.context = context;
        this.period = period;
        this.message = message;
    }

}
