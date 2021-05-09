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
import { PluginManifest, PluginStore } from "graylog-web-plugin/plugin";

import TeamsNotificationForm from "./TeamsNotificationForm";
import TeamsNotificationSummary from "./TeamsNotificationSummary";

PluginStore.register(
  new PluginManifest(
    {},
    {
      eventNotificationTypes: [
        {
          type: "teams-notification-v2",
          displayName: "Microsoft Teams Notification",
          formComponent: TeamsNotificationForm,
          summaryComponent: TeamsNotificationSummary,
          defaultConfig: TeamsNotificationForm.defaultConfig
        }
      ]
    }
  )
);
