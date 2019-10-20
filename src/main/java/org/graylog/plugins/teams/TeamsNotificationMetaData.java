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
        return "org.graylog.plugins.teams.TeamsPlugin";
    }

    @Override
    public String getName() {
        return "Teams";
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
        return "Microsoft Teams plugin";
    }

    @Override
    public Version getRequiredVersion() {
        return Version.fromPluginProperties(getClass(), PLUGIN_PROPERTIES, "graylog.version", Version.from(0, 0, 0, "unknown"));
    }

    @Override
    public Set<ServerStatus.Capability> getRequiredCapabilities() {
        return Collections.emptySet();
    }
}
