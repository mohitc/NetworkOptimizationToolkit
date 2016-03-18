package com.network.topology.models.extractors;

public class ModelExtractionException extends Exception {

  private String message;

  public ModelExtractionException(String message) {
    this.message = message;
  }

  public ModelExtractionException(String message, Exception e) {
    this.message = message;
    this.setStackTrace(e.getStackTrace());
  }

  public String getMessage() {
    return message;
  }

}
