package org.graylog.plugins.teams;

import java.util.Collection;
import java.util.Collections;
import org.graylog2.plugin.Plugin;
import org.graylog2.plugin.PluginMetaData;
import org.graylog2.plugin.PluginModule;

public class TeamsNotificationPlugin implements Plugin {
    @Override
    public PluginMetaData metadata() {
        return new TeamsNotificationMetaData();
    }

    @Override
    public Collection<PluginModule> modules () {
        return Collections.<PluginModule>singletonList(new TeamsNotificationModule());
    }
}
