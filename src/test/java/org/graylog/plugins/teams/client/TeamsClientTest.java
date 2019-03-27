package org.graylog.plugins.teams.client;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.graylog.plugins.teams.alerts.TeamsNotificationConfig;
import org.graylog2.plugin.configuration.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

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
  void initTeamsClient_Fail_InvalidProxy() {
    Map<String, Object> m = createValidConfigMap();
    m.put(TeamsNotificationConfig.PROXY, "invalid proxy");
    Configuration invalidConf = new Configuration(m);

    TeamsClientException ex = assertThrows(TeamsClientException.class, () -> new TeamsClient(invalidConf));
    assertEquals("Proxy URI is invalid format. URI=invalid proxy", ex.getMessage());
  }

  private Map<String, Object> createValidConfigMap() {
    Map<String, Object> m = new HashMap<>();
    m.put(TeamsNotificationConfig.WEBHOOK_URL, "https://testwebhook.com");
    m.put(TeamsNotificationConfig.COLOR, "000000");
    m.put(TeamsNotificationConfig.DETAIL_MESSAGE, "Detail");
    return m;
  }

  @Nested
  class PostMessageCard {

    private TeamsClient sut;

    private MockWebServer server;

    @BeforeEach
    void setUp() {
      server = new MockWebServer();
    }

    @AfterEach
    void tearDown() throws IOException {
      server.shutdown();
    }

    @Test
    void postMessageCard() throws IOException {
      // Prepare mock server
      server.enqueue(new MockResponse().setResponseCode(200));
      server.start();

      // Prepare Configuration
      Map<String, Object> m = createValidConfigMap();
      m.replace(TeamsNotificationConfig.WEBHOOK_URL, server.url("/").toString());
      sut = new TeamsClient(new Configuration(m));

      // Then
      try {
        sut.postMessageCard(new TeamsMessageCard("FFFFFF", "Title", "Text", "Detail"));
      } catch (TeamsClientException ex) {
        fail("Exception should not be thrown");
      }
    }

    @Test
    void postMessageCard_Fail_InvalidWebhookURL() {
      Map<String, Object> m = createValidConfigMap();
      m.replace(TeamsNotificationConfig.WEBHOOK_URL, "invalid webhook$$$");
      sut = new TeamsClient(new Configuration(m));

      // Then
      TeamsClientException ex = assertThrows(TeamsClientException.class,
          () -> sut.postMessageCard(new TeamsMessageCard("FFFFFF", "Title", "Text", "Detail")));
      assertEquals("Teams webhook URL is invalid format. URL=invalid webhook$$$", ex.getMessage());
    }

    @Test
    void postMessageCard_Fail_UnexpectedRequestCode() throws IOException {
      // Prepare mock server
      server.enqueue(new MockResponse().setResponseCode(500));
      server.start();

      // Prepare Configuration
      Map<String, Object> m = createValidConfigMap();
      m.replace(TeamsNotificationConfig.WEBHOOK_URL, server.url("/").toString());
      sut = new TeamsClient(new Configuration(m));

      // Then
      TeamsClientException ex = assertThrows(TeamsClientException.class,
          () -> sut.postMessageCard(new TeamsMessageCard("FFFFFF", "Title", "Text", "Detail")));
      assertEquals("Teams webhook returned unexpected response status. HTTP Status=500", ex.getMessage());
    }
  }
}
