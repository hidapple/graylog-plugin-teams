package org.graylog.plugins.teams.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class TeamsMessageCard {

  private String type = "MessageCart";
  private String context = "https://schema.org/extensions";
  private String themeColor;
  private String title;
  private String text;

  public TeamsMessageCard(String color, String title, String text) {
    this.setThemeColor(color);
    this.setTitle(title);
    this.setText(text);
  }

  public String toJsonString() {
    Map<String, Object> params = new HashMap<>();
    params.put("@type", type);
    params.put("@context", context);
    params.put("themeColor", themeColor);
    params.put("title", title);
    params.put("text", text);

    try {
      return new ObjectMapper().writeValueAsString(params);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to build request payload as JSON format.");
    }
  }

  private void setThemeColor(String color) {
    this.themeColor = color;
  }

  private void setTitle(String title) {
    this.title = title;
  }

  private void setText(String text) {
    this.text = text;
  }
}
