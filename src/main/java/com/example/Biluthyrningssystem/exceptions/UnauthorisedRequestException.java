package com.example.Biluthyrningssystem.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UnauthorisedRequestException extends RuntimeException {
    private String object;
    private Object value;
    private String request;
    private String reason;

    public UnauthorisedRequestException(String object, Object value, String request, String reason) {
        super(String.format("%s [%s] is not authorised to %s - %s", object,value,request,reason));
        this.object =object;
        this.value = value;
        this.request = request;
        this.reason = reason;
    }

    public String getObject() {
        return object;
    }

    public Object getValue() {
        return value;
    }

    public String getRequest() {
        return request;
    }

    public String getReason() {
        return reason;
    }
}
