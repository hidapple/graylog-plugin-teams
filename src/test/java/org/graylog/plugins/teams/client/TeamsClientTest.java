package org.graylog.plugins.teams.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.graylog.plugins.teams.alerts.TeamsNotificationConfig;
import org.graylog2.plugin.configuration.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

class TeamsClientTest {

  @Test
  void initTeamsClient_OK_WithoutProxy() {
    try {
      new TeamsClient(new Configuration(createValidConfigMap()));
    } catch (TeamsClientException ex) {
      fail("Exception should not be thrown");
    }
  }

  @Test
  void initTeamsClient_OK_WithProxy() {
    Map<String, Object> m = createValidConfigMap();
    m.put(TeamsNotificationConfig.PROXY, "http://proxy.com:9999");
    try {
      new TeamsClient(new Configuration(m));
    } catch (TeamsClientException ex) {
      fail("Exception should not be thrown");
    }
  }

  @Test
  void initTeamsClient_Fail_InvalidWebhook() {
    Map<String, Object> m = new HashMap<>();
    m.put(TeamsNotificationConfig.WEBHOOK_URL, "invalid webhook");
    Configuration invalidConf = new Configuration(m);

    TeamsClientException ex = assertThrows(TeamsClientException.class, () -> new TeamsClient(invalidConf));
    assertEquals("Teams webhook URL is invalid format. URL=invalid webhook", ex.getMessage());
  }

  @Test
  void initTeamsClient_Fail_InvalidProxy() {
    Map<String, Object> m = createValidConfigMap();
    m.put(TeamsNotificationConfig.PROXY, "invalid proxy");
    Configuration invalidConf = new Configuration(m);

    TeamsClientException ex = assertThrows(TeamsClientException.class, () -> new TeamsClient(invalidConf));
    assertEquals("Proxy URI is invalid format. URI=invalid proxy", ex.getMessage());
  }

  private Map<String, Object> createValidConfigMap() {
    Map<String, Object> m = new HashMap<>();
    m.put(TeamsNotificationConfig.WEBHOOK_URL, "http://localhost:8090");
    m.put(TeamsNotificationConfig.COLOR, "000000");
    m.put(TeamsNotificationConfig.DETAIL_MESSAGE, "Detail");
    return m;
  }

  @Nested
  class PostMessageCard {

    private TeamsClient sut;

    private WireMockServer server;

    @BeforeEach
    void setUp() {
      server = new WireMockServer(8090);
      server.start();
    }

    @AfterEach
    void tearDown() {
      server.stop();
    }

    @Test
    void postMessageCard() {
      // Prepare config
      Map<String, Object> m = createValidConfigMap();
      sut = new TeamsClient(new Configuration(m));

      // Prepare mock server
      server.stubFor(
          post(urlEqualTo("/"))
              .willReturn(aResponse().withStatus(200))
      );

      // Then
      try {
        sut.postMessageCard(new TeamsMessageCard("FFFFFF", "Title", "Text", "Detail", ""));
      } catch (Exception ex) {
        fail("Exception should not be thrown.", ex);
      }
    }

    @Test
    void postMessageCard_Fail_UnexpectedRequestCode() {
      // Prepare Configuration
      Map<String, Object> m = createValidConfigMap();
      sut = new TeamsClient(new Configuration(m));

      // Prepare mock server
      server.stubFor(
          post(urlEqualTo("/"))
              .willReturn(aResponse().withStatus(500))
      );

      // Then
      TeamsClientException ex = assertThrows(TeamsClientException.class,
          () -> sut.postMessageCard(new TeamsMessageCard("FFFFFF", "Title", "Text", "Detail", "")));
      assertEquals("Teams webhook returned unexpected response status. HTTP Status=500", ex.getMessage());
    }
  }
}
