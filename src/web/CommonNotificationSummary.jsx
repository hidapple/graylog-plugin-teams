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
import { Table, Button } from "react-bootstrap";
import styles from "CommonNotificationSummary.css";

class CommonNotificationSummary extends React.Component {
  static propTypes = {
    type: PropTypes.string.isRequired,
    notification: PropTypes.object.isRequired,
    definitionNotification: PropTypes.object.isRequired,
    children: PropTypes.element.isRequired
  };

  state = {
    displayDetails: false
  };

  toggleDisplayDetails = () => {
    const { displayDetails } = this.state;
    this.setState({ displayDetails: !displayDetails });
  };

  render() {
    const { type, notification, definitionNotification, children } = this.props;
    const { displayDetails } = this.state;
    return (
      <React.Fragment>
        <h4>{notification.title || definitionNotification.notification_id}</h4>
        <dl>
          <dd>{type}</dd>
          <dd>
            <Button
              bsStyle="link"
              className="btn-text"
              bsSize="xsmall"
              onClick={this.toggleDisplayDetails}
            >
              <i
                className={`fa fa-caret-${displayDetails ? "down" : "right"}`}
              />
              &nbsp;
              {displayDetails ? "Less details" : "More details"}
            </Button>
            {displayDetails && (
              <Table condensed hover className={styles.fixedTable}>
                <tbody>
                  <tr>
                    <td>Description</td>
                    <td>
                      {notification.description || "No description given"}
                    </td>
                  </tr>
                  {children}
                </tbody>
              </Table>
            )}
          </dd>
        </dl>
      </React.Fragment>
    );
  }
}

export default CommonNotificationSummary;
