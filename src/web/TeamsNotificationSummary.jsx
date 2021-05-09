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
import React from "react";
import PropTypes from "prop-types";

import CommonNotificationSummary from "./CommonNotificationSummary";

class TeamsNotificationSummary extends React.Component {
  static propTypes = {
    type: PropTypes.string.isRequired,
    notification: PropTypes.object,
    definitionNotification: PropTypes.object.isRequired
  };

  static defaultProps = {
    notification: {}
  };

  render() {
    const { notification } = this.props;
    return (
      <CommonNotificationSummary {...this.props}>
        <React.Fragment>
          <tr>
            <td>Webhook URL</td>
            <td>{notification.config.webhook_url}</td>
          </tr>
          <tr>
            <td>Graylog URL</td>
            <td>{notification.config.graylog_url}</td>
          </tr>
          <tr>
            <td>Color</td>
            <td>{notification.config.color}</td>
          </tr>
          <tr>
            <td>Message</td>
            <td>{notification.config.message}</td>
          </tr>
          <tr>
            <td>Proxy URL</td>
            <td>{notification.config.proxy_url}</td>
          </tr>
        </React.Fragment>
      </CommonNotificationSummary>
    );
  }
}

export default TeamsNotificationSummary;
