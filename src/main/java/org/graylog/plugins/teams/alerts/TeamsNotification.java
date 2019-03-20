package org.graylog.plugins.teams.alerts;

import java.util.Map;
import org.graylog.plugins.teams.client.TeamsClient;
import org.graylog.plugins.teams.client.TeamsWebhookRequest;
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

  @Override
  public void initialize(Configuration config) throws AlarmCallbackConfigurationException {
    // TODO: Check configuration and throw Exception if it's invalid
    this.configuration = config;
  }

  @Override
  public void call(Stream stream, AlertCondition.CheckResult result) throws AlarmCallbackException {
    TeamsClient client = new TeamsClient(configuration);
    TeamsWebhookRequest req = new TeamsWebhookRequest(
        configuration.getString(TeamsNotificationConfig.MESSAGE));
    client.send(req);
  }

  @Override
  public ConfigurationRequest getRequestedConfiguration() {
    ConfigurationRequest configRequest = new ConfigurationRequest();

    configRequest.addField(new TextField(
        TeamsNotificationConfig.WEBHOOK_URL, "Webhook URL", "",
        "Microsoft Teams Incoming Webhook URL", Optional.NOT_OPTIONAL));

    // TODO: For now just support simple text message but should support rich message
    configRequest.addField(new TextField(
        TeamsNotificationConfig.MESSAGE, "Message", "",
        "Notification message", Optional.NOT_OPTIONAL, Attribute.TEXTAREA));

    configRequest.addField(new TextField(
        TeamsNotificationConfig.PROXY, "Proxy", "",
        "Proxy URL", Optional.OPTIONAL));

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

  }
}
