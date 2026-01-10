package com.library.api.service.exception;

public class LoanException extends RuntimeException {

  // To catch other Exceptions
  public LoanException(String message, Throwable cause) {
    super(message, cause);
  }

  // To validate business logic
  public LoanException(String message) {
    super(message);
  }
}
