package org.graylog.plugins.teams;

import org.graylog.plugins.teams.alerts.TeamsNotification;
import org.graylog2.plugin.PluginConfigBean;
import org.graylog2.plugin.PluginModule;

import java.util.Collections;
import java.util.Set;

public class TeamsModule extends PluginModule {

  @Override
  public Set<? extends PluginConfigBean> getConfigBeans() {
    return Collections.emptySet();
  }

  @Override
  protected void configure() {
    addAlarmCallback(TeamsNotification.class);
  }
}
