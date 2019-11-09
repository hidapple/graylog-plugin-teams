package org.graylog.plugins.teams.client;

public class TeamsClientException extends RuntimeException {

  private static final long serialVersionUID = -2367942499007524159L;

  public TeamsClientException() {
    super();
  }

  public TeamsClientException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public TeamsClientException(final String message) {
    super(message);
  }
}
