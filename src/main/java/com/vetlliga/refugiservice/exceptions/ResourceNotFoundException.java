package com.vetlliga.refugiservice.exceptions;

public class ResourceNotFoundException extends RuntimeException {

  public ResourceNotFoundException(String message) {
    super(message);
  }
}
