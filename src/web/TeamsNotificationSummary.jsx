import React from 'react';
import PropTypes from 'prop-types';

import CommonNotificationSummary from './CommonNotificationSummary';

// import styles from './TeamsNotificationSummary.css';

class TeamsNotificationSummary extends React.Component {
  static propTypes = {
    type: PropTypes.string.isRequired,
    notification: PropTypes.object,
    definitionNotification: PropTypes.object.isRequired,
  };

  static defaultProps = {
    notification: {},
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
