package org.graylog.plugins.teams.client;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

class TeamsMessageCardTest {

  @Test
  void toJsonString_WithoutDetailMsgAndGraylogURL() throws IOException {
    TeamsMessageCard sut = new TeamsMessageCard("0076D7", "Title", "Text", StringUtils.EMPTY, StringUtils.EMPTY);
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

  @Test
  void toJsonString_WithGraylogURL() throws IOException {
    TeamsMessageCard sut = new TeamsMessageCard("0076D7", "Title", "Text", StringUtils.EMPTY, "http://localhost:9000");
    String expected = "{"
        + "\"@type\":\"MessageCard\","
        + "\"@context\":\"https://schema.org/extensions\","
        + "\"themeColor\":\"0076D7\","
        + "\"title\":\"Title\","
        + "\"text\":\"Text\","
        + "\"potentialAction\":[{"
        + "\"@type\":\"OpenUri\","
        + "\"name\":\"Open Graylog\","
        + "\"targets\":[{"
        + "\"os\":\"default\","
        + "\"uri\":\"http://localhost:9000\""
        + "}]"
        + "}]"
        + "}";

    // When
    String actual = sut.toJsonString();

    // Then
    assertJSON(expected, actual);
  }

  private void assertJSON(final String json1, final String json2) throws IOException {
    final ObjectMapper mapper = new ObjectMapper();
    final JsonNode node1 = mapper.readTree(json1);
    final JsonNode node2 = mapper.readTree(json2);

    assertEquals(node1, node2);
  }
}