package com.library.api.service.exception;

public class LoanException extends RuntimeException {
  public LoanException(String message, Throwable cause) {
    super(message, cause);
  }
}
