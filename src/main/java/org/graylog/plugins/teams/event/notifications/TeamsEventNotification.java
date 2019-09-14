package org.graylog.plugins.teams.event.notifications;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;
import org.graylog.events.notifications.EventNotification;
import org.graylog.events.notifications.EventNotificationContext;
import org.graylog.events.notifications.EventNotificationException;
import org.graylog.events.notifications.EventNotificationModelData;
import org.graylog.events.notifications.EventNotificationService;
import org.graylog.events.notifications.PermanentEventNotificationException;
import org.graylog.events.processor.EventDefinitionDto;
import org.graylog2.jackson.TypeReferences;
import org.graylog2.plugin.MessageSummary;

public class TeamsEventNotification implements EventNotification {
  public interface Factory extends EventNotification.Factory {
    @Override
    TeamsEventNotification create();
  }

  private static final String UNKNOWN = "<unknown>";

  private final EventNotificationService notificationCallbackService;
  private final TeamsClient teamsClient;
  private final ObjectMapper objMapper;

  @Inject
  public TeamsEventNotification(final EventNotificationService notificationCallbackService,
      final TeamsClient client,
      final ObjectMapper objMapper) {
    this.notificationCallbackService = notificationCallbackService;
    this.teamsClient = client;
    this.objMapper = objMapper;
  }

  @Override
  public void execute(final EventNotificationContext ctx) throws EventNotificationException {
    final TeamsEventNotificationConfig config = (TeamsEventNotificationConfig) ctx.notificationConfig();
    final ImmutableList<MessageSummary> backlog = notificationCallbackService.getBacklogForEvent(ctx);
    try {
      teamsClient.postMessageCard(config, getModel(ctx, backlog));
    } catch (final TeamsClientException e) {
      throw new PermanentEventNotificationException("TeamsEventNotification is triggered but failed sending request", e);
    }
  }

  private Map<String, Object> getModel(final EventNotificationContext ctx, final ImmutableList<MessageSummary> backlog) {
    final Optional<EventDefinitionDto> definitionDto = ctx.eventDefinition();
    final EventNotificationModelData modelData = EventNotificationModelData.builder()
        .eventDefinitionId(definitionDto.map(EventDefinitionDto::id).orElse(UNKNOWN))
        .eventDefinitionType(definitionDto.map(d -> d.config().type()).orElse(UNKNOWN))
        .eventDefinitionTitle(definitionDto.map(EventDefinitionDto::title).orElse(UNKNOWN))
        .eventDefinitionDescription(definitionDto.map(EventDefinitionDto::description).orElse(UNKNOWN))
        .jobDefinitionId(ctx.jobTrigger().jobDefinitionId())
        .jobTriggerId(ctx.jobTrigger().id())
        .event(ctx.event())
        .backlog(backlog)
        .build();

    return objMapper.convertValue(modelData, TypeReferences.MAP_STRING_OBJECT);
  }
}
