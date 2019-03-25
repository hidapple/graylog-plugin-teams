package org.graylog.plugins.teams.client;

public class TeamsClientException extends RuntimeException {

  public TeamsClientException() {
    super();
  }

  public TeamsClientException(String message, Throwable cause) {
    super(message, cause);
  }

  public TeamsClientException(String message) {
    super(message);
  }
}
