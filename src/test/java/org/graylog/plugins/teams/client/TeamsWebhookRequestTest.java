package org.graylog.plugins.teams.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TeamsWebhookRequestTest {

  @Test
  void toJsonString_ConvertToJsonString() {
    TeamsWebhookRequest sut = new TeamsWebhookRequest("test message");
    String expected = "{\"text\":\"test message\"}";

    String actual = sut.toJsonString();

    Assertions.assertEquals(expected, actual);
  }
}
