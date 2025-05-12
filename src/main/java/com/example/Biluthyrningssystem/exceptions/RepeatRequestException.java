package com.example.Biluthyrningssystem.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RepeatRequestException extends RuntimeException {

  private String object;
  private String field;
  private Object value;
  private String message;

  public RepeatRequestException(String object, String field, Object value, String message) {
    super(String.format("%s with %s '%s' : %s", object, field, value, message));
    this.object = object;
    this.field = field;
    this.value = value;
    this.message = message;
  }
}
