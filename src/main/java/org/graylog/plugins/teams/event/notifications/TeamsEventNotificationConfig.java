package org.graylog.plugins.teams.event.notifications;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import org.graylog.events.contentpack.entities.EventNotificationConfigEntity;
import org.graylog.events.event.EventDto;
import org.graylog.events.notifications.EventNotificationConfig;
import org.graylog.events.notifications.EventNotificationExecutionJob;
import org.graylog.scheduler.JobTriggerData;
import org.graylog2.contentpacks.EntityDescriptorIds;
import org.graylog2.plugin.rest.ValidationResult;

import javax.validation.constraints.NotBlank;

@AutoValue
@JsonTypeName(TeamsEventNotificationConfig.TYPE_NAME)
@JsonDeserialize(builder = TeamsEventNotificationConfig.Builder.class)
public abstract class TeamsEventNotificationConfig implements EventNotificationConfig {
  public static final String TYPE_NAME = "teams-notification-v2";

  private static final String FIELD_WEBHOOK_URL = "webhook_url";
  private static final String FIELD_GRAYLOG_URL = "graalog_url";
  private static final String FIELD_COLOR = "color";
  private static final String FIELD_MESSAGE = "message";
  private static final String FIELD_PROXY_URL = "proxy_url";

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
  public JobTriggerData toJobTriggerData(EventDto dto) {
    return EventNotificationExecutionJob.Data.builder().eventDto(dto).build();
  }

  // TODO
  @JsonIgnore
  public ValidationResult validate() {
    final ValidationResult validation = new ValidationResult();
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
          .color("")
          .message("")
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
  public EventNotificationConfigEntity toContentPackEntity(EntityDescriptorIds entityDescriptorIds) {
    return null;
  }
}
