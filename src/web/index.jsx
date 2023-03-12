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
import 'webpack-entry';

import { PluginManifest, PluginStore } from "graylog-web-plugin/plugin";

import TeamsNotificationForm from "./TeamsNotificationForm";
import TeamsNotificationSummary from "./TeamsNotificationSummary";
import TeamsNotificationDetails from './TeamsNotificationDetails';

import packageJson from '../../package.json';

PluginStore.register(
  new PluginManifest(packageJson,
    {
      eventNotificationTypes: [
        {
          type: "teams-notification-v2",
          displayName: "Microsoft Teams Notification V2",
          formComponent: TeamsNotificationForm,
          summaryComponent: TeamsNotificationSummary,
          detailsComponent: TeamsNotificationDetails,
          defaultConfig: TeamsNotificationForm.defaultConfig
        }
      ]
    }
  )
);
