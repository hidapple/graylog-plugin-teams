import { PluginManifest, PluginStore } from 'graylog-web-plugin/plugin';

import TeamsNotificationForm from './TeamsNotificationForm';
import TeamsNotificationSummary from './TeamsNotificationSummary';

PluginStore.register(new PluginManifest({}, {
  eventNotificationTypes: [
    {
      type: 'teams-notification-v2',
      displayName: 'Teams Notification',
      formComponent: TeamsNotificationForm,
      summaryComponent: TeamsNotificationSummary,
      defaultConfig: TeamsNotificationForm
    }
  ]
}));
