package org.graylog.plugins.teams.client;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * MessageCard is representing Outlook Actionable Message Card request.
 * https://docs.microsoft.com/en-us/outlook/actionable-messages/message-card-reference
 */
public class TeamsMessageCard {

  private String type;
  private String context;
  private String themeColor;
  private String title;
  private String text;
  private List<Section> sections;
  private List<PotentialAction> potentialAction;

  public TeamsMessageCard(String color, String title, String text, String detailMsg, String url) {
    this.type = "MessageCard";
    this.context = "https://schema.org/extensions";
    this.themeColor = color;
    this.title = title;
    this.text = text;
    if (!StringUtils.isEmpty(detailMsg)) {
      this.sections = Lists.newArrayList(new Section("Detail Message:", detailMsg));
    }
    if (!StringUtils.isEmpty(url)) {
      Map<String, String> target = new HashMap<>();
      target.put("os", "default");
      target.put("uri", url);
      this.potentialAction = Lists.newArrayList(
          new PotentialAction("OpenUri", "Open Graylog", Lists.newArrayList(target)));
    }
  }

  public String toJsonString() {
    Map<String, Object> params = new HashMap<>();
    params.put("@type", type);
    params.put("@context", context);
    params.put("themeColor", themeColor);
    params.put("title", title);
    params.put("text", text);
    if (Objects.nonNull(sections)) {
      params.put("sections", sections);
    }
    if (Objects.nonNull(potentialAction)) {
      params.put("potentialAction", potentialAction);
    }

    try {
      return new ObjectMapper().writeValueAsString(params);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to build Teams MessageCard payload as JSON format.");
    }
  }

  @JsonInclude(Include.NON_NULL)
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Section {
    @JsonProperty("title")
    public String title;
    @JsonProperty("text")
    public String text;

    @JsonCreator
    public Section(String title, String text) {
      this.title = title;
      this.text = text;
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
    PotentialAction(String type, String name, List<Map<String, String>> targets) {
      this.type = type;
      this.name = name;
      this.targets = targets;
    }
  }

}
