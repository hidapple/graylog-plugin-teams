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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MessageCard is representing Adaptive Card request via Microsoft Workflow webhook.
 * https://learn.microsoft.com/en-us/microsoftteams/platform/task-modules-and-cards/cards/cards-reference#adaptive-card
 */
public class TeamsMessageCard {

  private final String title;
  private final String titleColor;
  private final String text;
  private CardAction action; // only first message is linked

  public TeamsMessageCard(String title, String titleColor, String text, String firstMessageUrl) {
    this.title = StringUtils.isEmpty(title) ? null : title;
    this.titleColor = titleColor;
    this.text = text;

    if (firstMessageUrl != null && !firstMessageUrl.isBlank()) {
      this.action = new CardAction("Open Graylog", firstMessageUrl);
    }
  }

  public String toJsonString() {
    final Map<String, Object> titleBlock = new HashMap<>();
    titleBlock.put("type", "TextBlock");
    titleBlock.put("size", "Medium");
    titleBlock.put("weight", "Bolder");
    titleBlock.put("text", this.title);
    titleBlock.put("color", this.titleColor);
    final Map<String, Object> descBlock = new HashMap<>();
    descBlock.put("type", "TextBlock");
    descBlock.put("text", this.text);
    descBlock.put("wrap", true);

    final Map<String, Object> content = new HashMap<>();
    content.put("$schema", "https://adaptivecards.io/schemas/adaptive-card.json");
    content.put("type", "AdaptiveCard");
    content.put("version", "1.4"); // 1.4 is latest that works in Teams app
    content.put("msteams", Map.of("width", "full")); // needed for proper card width in Teams
    content.put("body", List.of(titleBlock, descBlock));
    if (action != null) {
      content.put("actions", List.of(action));
    }

    final Map<String, Object> attachment = new HashMap<>();
    attachment.put("contentType", "application/vnd.microsoft.card.adaptive");
    attachment.put("content", content);

    final Map<String, Object> request = new HashMap<>();
    request.put("type", "message");
    request.put("attachments", List.of(attachment));

    try {
      return new ObjectMapper().writeValueAsString(request);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Failed to build TeamsMessageCard payload as JSON format.", e);
    }
  }

  @JsonInclude(Include.NON_NULL)
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class CardAction {
    @JsonProperty("type")
    String type;
    @JsonProperty("title")
    String title;
    @JsonProperty("url")
    String url;

    @JsonCreator
    CardAction(String title, String url) {
      this.type = "Action.OpenUrl";
      this.title = title;
      this.url = url;
    }
  }
}
