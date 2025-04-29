package com.example.Biluthyrningssystem.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IncorrectInputException extends RuntimeException {
    private String resource;
    private String attribute;
    private Object value;
    private String format;
    private String additionalMessage;

    public IncorrectInputException(String resource,String attribute, Object value, String format, String additionalMessage) {
        super(String.format("%s attribute - %s, with value %s, is formatted incorrectly. Enter data with the following format %s. %s", resource,attribute,value,format,additionalMessage));
        this.resource = resource;
        this.attribute = attribute;
        this.value = value;
        this.format = format;
        this.additionalMessage = additionalMessage;
    }

    public String getResource() {
        return resource;
    }

    public String getAttribute() {
        return attribute;
    }

    public Object getValue() {
        return value;
    }

    public String getFormat() {
        return format;
    }

    public String getAdditionalMessage() {
        return additionalMessage;
    }
}
