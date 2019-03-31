package org.graylog.plugins.teams.client;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Test;

class TeamsMessageCardTest {

  @Test
  void toJsonString_WithDetailMsg() throws IOException {
    TeamsMessageCard sut = new TeamsMessageCard("0076D7", "Title", "Text", "Detail Message Text");
    String expected = "{"
        + "\"@type\":\"MessageCard\","
        + "\"@context\":\"https://schema.org/extensions\","
        + "\"themeColor\":\"0076D7\","
        + "\"title\":\"Title\","
        + "\"text\":\"Text\","
        + "\"sections\":[{"
        + "\"title\":\"Detail Message:\","
        + "\"text\":\"Detail Message Text\""
        + "}]"
        + "}";

    // When
    String actual = sut.toJsonString();

    // Then
    assertJSON(expected, actual);
  }

  @Test
  void toJsonString_WithoutDetailMsg() throws IOException {
    TeamsMessageCard sut = new TeamsMessageCard("0076D7", "Title", "Text", StringUtils.EMPTY);
    String expected = "{"
        + "\"@type\":\"MessageCard\","
        + "\"@context\":\"https://schema.org/extensions\","
        + "\"themeColor\":\"0076D7\","
        + "\"title\":\"Title\","
        + "\"text\":\"Text\""
        + "}";

    // When
    String actual = sut.toJsonString();

    // Then
    assertJSON(expected, actual);
  }

  private void assertJSON(String json1, String json2) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode node1 = mapper.readTree(json1);
    JsonNode node2 = mapper.readTree(json2);

    assertEquals(node1, node2);
  }
}
