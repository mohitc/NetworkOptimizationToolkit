package com.network.topology.models.validators;

public class ModelValidationException extends Exception {

  private String message;

  public ModelValidationException(String message) {
    this.message = message;
  }

  public ModelValidationException(String message, Exception e) {
    this.message = message;
    this.setStackTrace(e.getStackTrace());
  }

  public String getMessage() {
    return message;
  }
}
