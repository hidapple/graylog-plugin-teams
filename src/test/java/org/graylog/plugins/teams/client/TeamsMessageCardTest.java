/*
 * graylog-plugin-teams - Graylog Microsoft Teams plugin
 * Copyright © 2021 Shohei Hida
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog.plugins.teams.client;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

class TeamsMessageCardTest {

  @Test
  void toJsonString_WithoutDetailMsgAndGraylogURL() throws IOException {
    TeamsMessageCard sut = new TeamsMessageCard("0076D7", "Title", "Text", new ArrayList<>());
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
    TeamsMessageCard sut = new TeamsMessageCard("0076D7", "Title", "Text",
        Lists.newArrayList("http://localhost:9000/messages/index/id"));
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
        + "\"uri\":\"http://localhost:9000/messages/index/id\""
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