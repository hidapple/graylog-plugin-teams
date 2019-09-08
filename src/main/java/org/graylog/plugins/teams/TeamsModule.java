package org.graylog.plugins.teams;

import org.graylog.events.notifications.EventNotification;
import org.graylog.events.notifications.EventNotificationConfig;
import org.graylog.plugins.teams.alerts.TeamsNotification;
import org.graylog2.plugin.PluginConfigBean;
import org.graylog2.plugin.PluginModule;

import java.util.Collections;
import java.util.Set;

public class TeamsModule extends PluginModule {

  private static final String TYPE_NAME = "teams-notification-v2.0.0";

  @Override
  public Set<? extends PluginConfigBean> getConfigBeans() {
    return Collections.emptySet();
  }

  @Override
  protected void configure() {
    addAlarmCallback(TeamsNotification.class);
    addNotificationType(TYPE_NAME, EventNotificationConfig.class, EventNotification.class, EventNotification.Factory.class);
  }
}
