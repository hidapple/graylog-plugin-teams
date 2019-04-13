package org.graylog.plugins.teams.alerts;

import com.floreysoft.jmte.Engine;
import com.google.common.collect.Lists;
import org.graylog.plugins.teams.client.TeamsClient;
import org.graylog.plugins.teams.client.TeamsClientException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * TeamsNotification is Graylog Notification(AlarmCallback) Plugin.
 */
public class TeamsNotification implements AlarmCallback {

  private static final Logger LOG = LoggerFactory.getLogger(TeamsNotification.class);

  private final Engine engine = new Engine();
  private Configuration configuration;

  @Override
  public void initialize(Configuration config) throws AlarmCallbackConfigurationException {
    this.configuration = config;
  }

  @Override
  public void call(Stream stream, AlertCondition.CheckResult result) throws AlarmCallbackException {
    TeamsClient client;
    try {
      client = new TeamsClient(configuration);
    } catch (TeamsClientException ex) {
      throw new AlarmCallbackException("Failed to create Teams webhook client", ex);
    }
    TeamsMessageCard req = new TeamsMessageCard(
        configuration.getString(TeamsNotificationConfig.COLOR),
        "Alert for Graylog stream: " + stream.getTitle(),
        result.getResultDescription(),
        buildDetailMsg(stream, result, configuration.getString(TeamsNotificationConfig.DETAIL_MESSAGE)),
        configuration.getString(TeamsNotificationConfig.GRAYLOG_URL)
    );
    System.out.println(req.toJsonString());
    try {
      client.postMessageCard(req);
    } catch(TeamsClientException ex) {
      throw new AlarmCallbackException("Failed to send POST request to Teams webhook.", ex);
    }
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
        TeamsNotificationConfig.GRAYLOG_URL, "Graylog URL",
        "",
        "URL to be attached in notification",
        Optional.OPTIONAL));

    configRequest.addField(new TextField(
        TeamsNotificationConfig.COLOR, "Color",
        "0076D7",
        "Color code",
        Optional.OPTIONAL));

    configRequest.addField(new TextField(
        TeamsNotificationConfig.DETAIL_MESSAGE, "Detail Message",
        "Alert Description: ${check_result.resultDescription}  \n" +
            "Date: ${check_result.triggeredAt}  \n" +
            "Stream ID: ${stream.id}  \n" +
            "Stream title: ${stream.title}  \n" +
            "Stream description: ${stream.description}  \n" +
            "Alert Condition Title: ${alert_condition.title}  \n" +
            "${if stream_url}Stream URL: ${stream_url}${end}  \n" +
            "Triggered condition: ${check_result.triggeredCondition}  \n" +
            "${if backlog}" +
            "${foreach backlog message}" +
            "${message}\n\n" +
            "${end}" +
            "${else}" +
            "<No backlog>\n" +
            "${end}",
        "Detail message supporting basic Markdown syntax",
        Optional.OPTIONAL,
        Attribute.TEXTAREA));

    configRequest.addField(new TextField(
        TeamsNotificationConfig.PROXY, "Proxy URL",
        "",
        "Proxy URL in the following format \"http(s)://${HOST}:${PORT}\".",
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
  public void checkConfiguration() throws ConfigurationException {
    if (!configuration.stringIsSet(TeamsNotificationConfig.WEBHOOK_URL)) {
      throw new ConfigurationException(TeamsNotificationConfig.WEBHOOK_URL + " is is mandatory and must not be empty.");
    }
    validateURI(configuration, TeamsNotificationConfig.WEBHOOK_URL);
    validateURI(configuration, TeamsNotificationConfig.PROXY);

    // Not error but warning
    if (configuration.stringIsSet(TeamsNotificationConfig.COLOR)) {
      String colorCode = configuration.getString(TeamsNotificationConfig.COLOR);
      if (!Objects.requireNonNull(colorCode).matches("[0-9a-fA-F]{6}|[0-9a-fA-F]{3}")) {
        LOG.warn("<{}> is invalid as color code. It will be ignored.", colorCode);
      }
    }
  }

  private String buildDetailMsg(Stream stream, AlertCondition.CheckResult result, String template) {
    List<Message> backlog = extractBacklog(result);
    Map<String, Object> model = getModel(stream, result, backlog);
    try {
      return engine.transform(template, model);
    } catch (Exception ex) {
      // In case of exception, just output exception message as custom message.
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

  private void validateURI(Configuration config,  String field) throws ConfigurationException {
    if (!config.stringIsSet(field)) return;
    try {
      new URI(Objects.requireNonNull(config.getString(field)));
    } catch (URISyntaxException ex) {
      throw new ConfigurationException(field + " is invalid as URI");
    }
  }
}
