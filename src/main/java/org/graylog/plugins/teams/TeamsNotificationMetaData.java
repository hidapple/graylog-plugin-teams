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

import java.net.URI;
import java.util.Collections;
import java.util.Set;
import org.graylog2.plugin.PluginMetaData;
import org.graylog2.plugin.ServerStatus;
import org.graylog2.plugin.Version;

public class TeamsNotificationMetaData implements PluginMetaData {

  private static final String PLUGIN_PROPERTIES = "org.graylog.plugins.graylog-plugin-teams/graylog-plugin.properties";

  @Override
  public String getUniqueId() {
    return "org.graylog.plugins.teams.TeamsNotificationPlugin";
  }

  @Override
  public String getName() {
    return "Microsoft Teams Notification V2"; // 'Microsoft Teams Notification' was added since Graylog 5
  }

  @Override
  public String getAuthor() {
    return "Shohei Hida";
  }

  @Override
  public URI getURL() {
    return URI.create("https://github.com/hidapple/graylog-plugin-teams");
  }

  @Override
  public Version getVersion() {
    return Version.fromPluginProperties(getClass(), PLUGIN_PROPERTIES, "version", Version.from(0, 0, 0, "unknown"));
  }

  @Override
  public String getDescription() {
    return "Microsoft Teams plugin with extra configuration";
  }

  @Override
  public Version getRequiredVersion() {
    return Version.fromPluginProperties(getClass(), PLUGIN_PROPERTIES, "graylog.version.required", Version.from(0, 0, 0, "unknown"));
  }

  @Override
  public Set<ServerStatus.Capability> getRequiredCapabilities() {
    return Collections.emptySet();
  }
}
