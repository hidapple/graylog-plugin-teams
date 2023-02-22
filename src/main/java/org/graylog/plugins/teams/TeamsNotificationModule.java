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
package org.graylog.plugins.teams;

import java.util.Collections;
import java.util.Set;
import org.graylog.plugins.teams.event.notifications.TeamsEventNotification;
import org.graylog.plugins.teams.event.notifications.TeamsEventNotificationConfig;
import org.graylog.plugins.teams.event.notifications.TeamsEventNotificationConfigEntity;
import org.graylog2.plugin.PluginConfigBean;
import org.graylog2.plugin.PluginModule;

public class TeamsNotificationModule extends PluginModule {

  @Override
  public Set<? extends PluginConfigBean> getConfigBeans() {
    return Collections.emptySet();
  }

  @Override
  protected void configure() {
    addNotificationType(TeamsEventNotificationConfig.TYPE_NAME,
                        TeamsEventNotificationConfig.class,
                        TeamsEventNotification.class,
                        TeamsEventNotification.Factory.class,
                        TeamsEventNotificationConfigEntity.TYPE_NAME,
                        TeamsEventNotificationConfigEntity.class);
  }
}
