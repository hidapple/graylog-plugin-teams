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

import static org.graylog.plugins.teams.event.notifications.TeamsEventNotificationConfig.FIELD_COLOR;
import static org.graylog.plugins.teams.event.notifications.TeamsEventNotificationConfig.FIELD_GRAYLOG_URL;
import static org.graylog.plugins.teams.event.notifications.TeamsEventNotificationConfig.FIELD_MESSAGE;
import static org.graylog.plugins.teams.event.notifications.TeamsEventNotificationConfig.FIELD_PROXY_URL;
import static org.graylog.plugins.teams.event.notifications.TeamsEventNotificationConfig.FIELD_WEBHOOK_URL;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import java.util.Map;
import javax.validation.constraints.NotBlank;
import org.graylog.events.contentpack.entities.EventNotificationConfigEntity;
import org.graylog.events.notifications.EventNotificationConfig;
import org.graylog2.contentpacks.model.entities.EntityDescriptor;
import org.graylog2.contentpacks.model.entities.references.ValueReference;

@AutoValue
@JsonTypeName(TeamsEventNotificationConfigEntity.TYPE_NAME)
@JsonDeserialize(builder = TeamsEventNotificationConfigEntity.Builder.class)
public abstract class TeamsEventNotificationConfigEntity implements EventNotificationConfigEntity {

  public static final String TYPE_NAME = "teams-notification-v2";

  @JsonProperty(FIELD_WEBHOOK_URL)
  @NotBlank
  public abstract ValueReference webhookURL();

  @JsonProperty(FIELD_GRAYLOG_URL)
  public abstract ValueReference graylogURL();

  @JsonProperty(FIELD_COLOR)
  public abstract ValueReference color();

  @JsonProperty(FIELD_MESSAGE)
  public abstract ValueReference message();

  @JsonProperty(FIELD_PROXY_URL)
  public abstract ValueReference proxyURL();

  public static Builder builder() {
    return Builder.create();
  }

  public abstract Builder toBuilder();

  @AutoValue.Builder
  public static abstract class Builder implements EventNotificationConfigEntity.Builder<Builder> {

    @JsonCreator
    public static Builder create() {
      return new AutoValue_TeamsEventNotificationConfigEntity.Builder().type(TYPE_NAME);
    }

    @JsonProperty(FIELD_WEBHOOK_URL)
    public abstract Builder webhookURL(ValueReference webhookURL);

    @JsonProperty(FIELD_GRAYLOG_URL)
    public abstract Builder graylogURL(ValueReference graylogURL);

    @JsonProperty(FIELD_COLOR)
    public abstract Builder color(ValueReference color);

    @JsonProperty(FIELD_MESSAGE)
    public abstract Builder message(ValueReference message);

    @JsonProperty(FIELD_PROXY_URL)
    public abstract Builder proxyURL(ValueReference proxyURL);

    public abstract TeamsEventNotificationConfigEntity build();
  }

  @Override
  public EventNotificationConfig toNativeEntity(Map<String, ValueReference> parameters, Map<EntityDescriptor, Object> nativeEntities) {
    return TeamsEventNotificationConfig.builder()
        .webhookURL(webhookURL().asString(parameters))
        .graylogURL(graylogURL().asString(parameters))
        .color(color().asString(parameters))
        .message(message().asString(parameters))
        .proxyURL(proxyURL().asString(parameters))
        .build();
  }
}
