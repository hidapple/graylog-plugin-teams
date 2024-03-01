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
import com.google.common.collect.Lists;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * MessageCard is representing Outlook Actionable Message Card request.
 * https://docs.microsoft.com/en-us/outlook/actionable-messages/message-card-reference
 */
public class TeamsMessageCard {

  private static final int POTENTIAL_ACTIONS_LIMIT = 3;

  private final String type;
  private final String context;
  private final String themeColor;
  private final String title;
  private final String text;
  private List<PotentialAction> potentialAction; // buttons

  public TeamsMessageCard(String color, String title, String text, List<String> urls) {
    this.type = "MessageCard";
    this.context = "https://schema.org/extensions";
    this.themeColor = color;
    this.title = StringUtils.isEmpty(title) ? null : title;
    this.text = text;

    if (!urls.isEmpty()) {
      this.potentialAction = new ArrayList<>();
      final int numberOfPotentialActions = Math.min(urls.size(), POTENTIAL_ACTIONS_LIMIT);
      for (int i = 1; i <= numberOfPotentialActions; i++) {
        final Map<String, String> target = new HashMap<>();
        target.put("os", "default");
        target.put("uri", urls.get(0));

        this.potentialAction.add(
            new PotentialAction(numberOfPotentialActions > 1 ? "Open Graylog - message " + i : "Open Graylog", Lists.newArrayList(target)));
      }
    }
  }

  public String toJsonString() {
    Map<String, Object> params = new HashMap<>();
    params.put("@type", type);
    params.put("@context", context);
    params.put("themeColor", themeColor);
    params.put("title", title);
    params.put("text", text);
    if (Objects.nonNull(potentialAction)) {
      params.put("potentialAction", potentialAction);
    }

    try {
      return new ObjectMapper().writeValueAsString(params);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Failed to build Teams MessageCard payload as JSON format.", e);
    }
  }

  @JsonInclude(Include.NON_NULL)
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class PotentialAction {
    @JsonProperty("@type")
    String type;
    @JsonProperty("name")
    String name;
    @JsonProperty("targets")
    List<Map<String, String>> targets;

    @JsonCreator
    PotentialAction(String name, List<Map<String, String>> targets) {
      this.type = "OpenUri";
      this.name = name;
      this.targets = targets;
    }
  }

}