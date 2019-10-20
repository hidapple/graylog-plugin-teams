package org.graylog.plugins.teams.event.notifications;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import javax.validation.constraints.NotBlank;
import org.graylog.events.contentpack.entities.EventNotificationConfigEntity;
import org.graylog.events.event.EventDto;
import org.graylog.events.notifications.EventNotificationConfig;
import org.graylog.events.notifications.EventNotificationExecutionJob;
import org.graylog.scheduler.JobTriggerData;
import org.graylog2.contentpacks.EntityDescriptorIds;
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
  public static final String FIELD_MESSAGE = "message";
  public static final String FIELD_PROXY_URL = "proxy_url";

  // Default values
  private static final String DEFAULT_COLOR = "0076D7";
  public static final String DEFAULT_MESSAGE = "Alert Description: ${check_result.resultDescription}\n" +
      "Date: ${check_result.triggeredAt}\n" +
      "Stream ID: ${stream.id}\n" +
      "Stream title: ${stream.title}\n" +
      "Stream description: ${stream.description}\n" +
      "Alert Condition Title: ${alert_condition.title}\n" +
      "${if stream_url}Stream URL: ${stream_url}${end}\n" +
      "Triggered condition: ${check_result.triggeredCondition}\n" +
      "${if backlog}" +
      "${foreach backlog message}" +
      "${message}\n\n" +
      "${end}" +
      "${else}" +
      "<No backlog>\n" +
      "${end}";

  @JsonProperty(FIELD_WEBHOOK_URL)
  @NotBlank
  public abstract String webhookURL();

  @JsonProperty(FIELD_GRAYLOG_URL)
  public abstract String graylogURL();

  @JsonProperty(FIELD_COLOR)
  public abstract String color();

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

  // TODO
  @JsonIgnore
  public ValidationResult validate() {
    final ValidationResult validation = new ValidationResult();

    if (webhookURL().isEmpty()) {
      validation.addError(FIELD_WEBHOOK_URL, "Webhook URL cannot be empty.");
    }
    return validation;
  }

  @AutoValue.Builder
  public static abstract class Builder implements EventNotificationConfig.Builder<Builder> {

    @JsonCreator
    public static Builder create() {
      return new AutoValue_TeamsEventNotificationConfig.Builder()
          .type(TYPE_NAME)
          .webhookURL("")
          .graylogURL("")
          .color(DEFAULT_COLOR)
          .message(DEFAULT_MESSAGE)
          .proxyURL("");
    }

    @JsonProperty(FIELD_WEBHOOK_URL)
    public abstract Builder webhookURL(String webhookURL);

    @JsonProperty(FIELD_GRAYLOG_URL)
    public abstract Builder graylogURL(String graylogURL);

    @JsonProperty(FIELD_COLOR)
    public abstract Builder color(String color);

    @JsonProperty(FIELD_MESSAGE)
    public abstract Builder message(String message);

    @JsonProperty(FIELD_PROXY_URL)
    public abstract Builder proxyURL(String proxyURL);

    public abstract TeamsEventNotificationConfig build();
  }

  // TODO
  @Override
  public EventNotificationConfigEntity toContentPackEntity(final EntityDescriptorIds entityDescriptorIds) {
    return TeamsEventNotificationConfigEntity.builder().build();
  }
}
