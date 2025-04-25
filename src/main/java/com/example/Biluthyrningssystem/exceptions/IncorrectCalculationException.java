package com.example.Biluthyrningssystem.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.EXPECTATION_FAILED)
public class IncorrectCalculationException extends RuntimeException {
    private String resource;
    private String field;
    private Object value;
    private String actual;
    public IncorrectCalculationException(String resource,String field, Object value, String actual) {
        super(String.format("%s with %s [%s] does not match calculated value [%s]. Enter calculated value or leave field empty to be automatically calulated", resource, field, value, actual));
        this.resource = resource;
        this.field = field;
        this.value = value;
        this.actual = actual;

    }

    public String getResource() {
        return resource;
    }

    public String getField() {
        return field;
    }

    public Object getValue() {
        return value;
    }

    public String getActual() {
        return actual;
    }
}
