package org.graylog.plugins.teams.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class TeamsMessageCard {

  private static String TYPE = "MessageCart";
  private static String CONTEXT = "https://schema.org/extensions";
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
    params.put("@type", TYPE);
    params.put("@context", CONTEXT);
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
    if (StringUtils.isEmpty(text)) {
      throw new IllegalArgumentException("Text field cannot be null or empty.");
    }
    this.text = text;
  }
}
