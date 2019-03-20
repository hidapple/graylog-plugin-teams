package org.graylog.plugins.teams.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;

public class TeamsWebhookRequest {

  private String text;

  public TeamsWebhookRequest(String text) {
    this.setText(text);
  }

  private void setText(String text) {
    if (StringUtils.isEmpty(text)) {
      throw new IllegalArgumentException("Text field cannot be null or empty.");
    }
    this.text = text;
  }

  public String toJsonString() {
    Map<String, Object> params = new HashMap<>();
    params.put("text", this.text);

    try {
      return new ObjectMapper().writeValueAsString(params);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to build request payload as JSON format.");
    }
  }
}
