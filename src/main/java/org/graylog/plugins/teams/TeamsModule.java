package org.graylog.plugins.teams;

import static org.graylog.plugins.teams.event.notifications.TeamsEventNotificationConfig.TYPE_NAME;

import java.util.Collections;
import java.util.Set;
import org.graylog.plugins.teams.event.notifications.TeamsEventNotification;
import org.graylog.plugins.teams.event.notifications.TeamsEventNotificationConfig;
import org.graylog2.plugin.PluginConfigBean;
import org.graylog2.plugin.PluginModule;

public class TeamsModule extends PluginModule {

  @Override
  public Set<? extends PluginConfigBean> getConfigBeans() {
    return Collections.emptySet();
  }

  @Override
  protected void configure() {
    addNotificationType(TYPE_NAME,
                        TeamsEventNotificationConfig.class,
                        TeamsEventNotification.class,
                        TeamsEventNotification.Factory.class);
  }
}
