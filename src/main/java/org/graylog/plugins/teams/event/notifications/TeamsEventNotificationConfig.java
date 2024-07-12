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
package org.graylog.plugins.teams.event.notifications;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import javax.validation.constraints.NotBlank;
import org.apache.commons.lang3.StringUtils;
import org.graylog.events.contentpack.entities.EventNotificationConfigEntity;
import org.graylog.events.event.EventDto;
import org.graylog.events.notifications.EventNotificationConfig;
import org.graylog.events.notifications.EventNotificationExecutionJob;
import org.graylog.scheduler.JobTriggerData;
import org.graylog2.contentpacks.EntityDescriptorIds;
import org.graylog2.contentpacks.model.entities.references.ValueReference;
import org.graylog2.plugin.rest.ValidationResult;

@AutoValue
@JsonTypeName(TeamsEventNotificationConfig.TYPE_NAME)
@JsonDeserialize(builder = TeamsEventNotificationConfig.Builder.class)
public abstract class TeamsEventNotificationConfig implements EventNotificationConfig {

  public static final String TYPE_NAME = "teams-notification-v2";

  // Plugin input fields
  public static final String FIELD_WEBHOOK_URL = "webhook_url";
  public static final String FIELD_GRAYLOG_URL = "graylog_url";
  public static final String FIELD_COLOR = "color";
  public static final String FIELD_CARD_TITLE = "card_title";
  public static final String FIELD_MESSAGE = "message"; // card body
  public static final String FIELD_PROXY_URL = "proxy_url";

  // Default values
  private static final String DEFAULT_COLOR = "0076D7";
  public static final String DEFAULT_CARD_TITLE = "Graylog Event Notification is triggered";
  /**
   * Card body
   */
  public static final String DEFAULT_MESSAGE = """
      # --- [Event Definition] ---------------------------
      **ID:**          ${event_definition_id}
      **Type:**        ${event_definition_type}
      **Title:**       ${event_definition_title}
      **Description:** ${event_definition_description}
      ****# --- [Event] --------------------------------------
      **Event:**                ${event}
      ****# --- [Event Detail] -------------------------------
      **Timestamp:**            ${event.timestamp}
      **Message:**              ${event.message}
      **Source:**               ${event.source}
      **Key:**                  ${event.key}
      **Priority:**             ${event.priority}
      **Alert:**                ${event.alert}
      **Timestamp Processing:** ${event.timestamp}
      **TimeRange Start:**      ${event.timerange_start}
      **TimeRange End:**        ${event.timerange_end}
      ${if event.fields}
      **Fields:**
      ${foreach event.fields field}  ${field.key}: ${field.value}
      ${end}
      ${if backlog}
      # --- [Backlog] ------------------------------------
      **Messages:**
      ${foreach backlog message}
      ```
      ${message}
      ```
      ${end}
      ${end}
      """;

  @JsonProperty(FIELD_WEBHOOK_URL)
  @NotBlank
  public abstract String webhookURL();

  @JsonProperty(FIELD_GRAYLOG_URL)
  public abstract String graylogURL();

  @JsonProperty(FIELD_COLOR)
  public abstract String color(); // title color

  @JsonProperty(FIELD_CARD_TITLE)
  public abstract String cardTitle();

  @JsonProperty(FIELD_MESSAGE)
  public abstract String message();

  @JsonProperty(FIELD_PROXY_URL)
  public abstract String proxyURL();

  public static Builder builder() {
    return Builder.create();
  }

  @JsonIgnore
  public JobTriggerData toJobTriggerData(final EventDto dto) {
    return EventNotificationExecutionJob.Data.builder().eventDto(dto).build();
  }

  @JsonIgnore
  public ValidationResult validate() {
    final ValidationResult validation = new ValidationResult();

    if (webhookURL().isEmpty()) {
      validation.addError(FIELD_WEBHOOK_URL, FIELD_WEBHOOK_URL + " cannot be empty.");
    }
    if (!validURL(webhookURL())) {
      validation.addError(FIELD_WEBHOOK_URL, FIELD_WEBHOOK_URL + " is invalid format.");
    }
    if (!validURL(graylogURL())) {
      validation.addError(FIELD_GRAYLOG_URL, FIELD_GRAYLOG_URL + " is invalid format.");
    }
    if (!validURL(proxyURL())) {
      validation.addError(FIELD_PROXY_URL, FIELD_PROXY_URL + " is invalid format.");
    }
    return validation;
  }

  @AutoValue.Builder
  public abstract static class Builder implements EventNotificationConfig.Builder<Builder> {

    @JsonCreator
    public static Builder create() {
      return new AutoValue_TeamsEventNotificationConfig.Builder()
          .type(TYPE_NAME)
          .webhookURL("")
          .graylogURL("")
          .color(DEFAULT_COLOR)
          .cardTitle(DEFAULT_CARD_TITLE)
          .message(DEFAULT_MESSAGE)
          .proxyURL("");
    }

    @JsonProperty(FIELD_WEBHOOK_URL)
    public abstract Builder webhookURL(String webhookURL);

    @JsonProperty(FIELD_GRAYLOG_URL)
    public abstract Builder graylogURL(String graylogURL);

    @JsonProperty(FIELD_COLOR)
    public abstract Builder color(String color);

    @JsonProperty(FIELD_CARD_TITLE)
    public abstract Builder cardTitle(String cardTitle);

    @JsonProperty(FIELD_MESSAGE)
    public abstract Builder message(String message);

    @JsonProperty(FIELD_PROXY_URL)
    public abstract Builder proxyURL(String proxyURL);

    public abstract TeamsEventNotificationConfig build();
  }

  @Override
  public EventNotificationConfigEntity toContentPackEntity(final EntityDescriptorIds entityDescriptorIds) {
    return TeamsEventNotificationConfigEntity.builder()
        .webhookURL(ValueReference.of(webhookURL()))
        .graylogURL(ValueReference.of(graylogURL()))
        .color(ValueReference.of(color()))
        .cardTitle(ValueReference.of(cardTitle()))
        .message(ValueReference.of(message()))
        .proxyURL(ValueReference.of(proxyURL()))
        .build();
  }

  private boolean validURL(final String uri) {
    if (StringUtils.isEmpty(uri)) {
      return true;
    }
    try {
      new URI(Objects.requireNonNull(uri));
    } catch (final URISyntaxException ex) {
      return false;
    }
    return true;
  }
}
