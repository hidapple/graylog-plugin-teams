package org.graylog.plugins.teams.alerts;

import com.floreysoft.jmte.Engine;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.graylog.plugins.teams.client.TeamsClient;
import org.graylog.plugins.teams.client.TeamsMessageCard;
import org.graylog2.plugin.Message;
import org.graylog2.plugin.MessageSummary;
import org.graylog2.plugin.alarms.AlertCondition;
import org.graylog2.plugin.alarms.callbacks.AlarmCallback;
import org.graylog2.plugin.alarms.callbacks.AlarmCallbackConfigurationException;
import org.graylog2.plugin.alarms.callbacks.AlarmCallbackException;
import org.graylog2.plugin.configuration.Configuration;
import org.graylog2.plugin.configuration.ConfigurationException;
import org.graylog2.plugin.configuration.ConfigurationRequest;
import org.graylog2.plugin.configuration.fields.ConfigurationField.Optional;
import org.graylog2.plugin.configuration.fields.TextField;
import org.graylog2.plugin.configuration.fields.TextField.Attribute;
import org.graylog2.plugin.streams.Stream;

/**
 * This is the plugin. Your class should implement one of the existing plugin
 * interfaces. (i.e. AlarmCallback, MessageInput, MessageOutput)
 */
public class TeamsNotification implements AlarmCallback {

  private Configuration configuration;
  private final Engine engine = new Engine();

  @Override
  public void initialize(Configuration config) throws AlarmCallbackConfigurationException {
    // TODO: Check configuration and throw Exception if it's invalid
    this.configuration = config;
  }

  @Override
  public void call(Stream stream, AlertCondition.CheckResult result) throws AlarmCallbackException {
    TeamsClient client = new TeamsClient(configuration);
    TeamsMessageCard req = new TeamsMessageCard(
        configuration.getString(TeamsNotificationConfig.COLOR),
        "Alert for Graylog stream: " + stream.getTitle(),
        result.getResultDescription(),
        buildCustomMsg(stream, result, configuration.getString(TeamsNotificationConfig.CUSTOM_MESSAGE))
    );
    client.send(req);
  }

  @Override
  public ConfigurationRequest getRequestedConfiguration() {
    ConfigurationRequest configRequest = new ConfigurationRequest();

    configRequest.addField(new TextField(
        TeamsNotificationConfig.WEBHOOK_URL, "Webhook URL",
        "",
        "Microsoft Teams Incoming Webhook URL",
        Optional.NOT_OPTIONAL));

    configRequest.addField(new TextField(
        TeamsNotificationConfig.COLOR, "Color",
        "0076D7",
        "Color code",
        Optional.NOT_OPTIONAL));

    configRequest.addField(new TextField(
        TeamsNotificationConfig.CUSTOM_MESSAGE, "Custom Message",
        "",
        "Notification message",
        Optional.OPTIONAL,
        Attribute.TEXTAREA));

    configRequest.addField(new TextField(
        TeamsNotificationConfig.PROXY, "Proxy",
        "",
        "Proxy URL",
        Optional.OPTIONAL));

    return configRequest;
  }

  @Override
  public String getName() {
    return "Microsoft Teams Alarm Callback";
  }

  @Override
  public Map<String, Object> getAttributes() {
    return configuration.getSource();
  }

  @Override
  public void checkConfiguration() throws ConfigurationException {}

  private String buildCustomMsg(Stream stream, AlertCondition.CheckResult result, String template) {
    List<Message> backlog = extractBacklog(result);
    Map<String, Object> model = getModel(stream, result, backlog);
    try {
      return engine.transform(template, model);
    } catch (Exception ex) {
      // In case of exception, just output exception message as custom message.
      ex.printStackTrace();
      return ex.toString();
    }
  }

  private List<Message> extractBacklog(AlertCondition.CheckResult result) {
    AlertCondition alertCondition = result.getTriggeredCondition();
    List<MessageSummary> matchingMessages = result.getMatchingMessages();
    int backlogSize = Math.min(alertCondition.getBacklog(), matchingMessages.size());

    if (backlogSize == 0) {
      return Collections.emptyList();
    }

    List<MessageSummary> backlogSummaries = matchingMessages.subList(0, backlogSize);
    List<Message> backlog = Lists.newArrayListWithCapacity(backlogSize);
    backlogSummaries.forEach(msgSum -> backlog.add(msgSum.getRawMessage()));
    return backlog;
  }

  private Map<String, Object> getModel(Stream stream, AlertCondition.CheckResult result, List<Message> backlog) {
    Map<String, Object> model = new HashMap<>();
    model.put("stream", stream);
    model.put("check_result", result);
    model.put("alert_condition", result.getTriggeredCondition());
    model.put("backlog", backlog);
    model.put("backlog_size", backlog.size());

    return model;
  }
}
