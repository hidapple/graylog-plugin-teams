import React from 'react';
import PropTypes from 'prop-types';
import lodash from 'lodash';

import {Input} from 'components/bootstrap';
import FormsUtils from 'util/FormsUtils';

const DEFAULT_MSG = `
--- [Event Definition] ---------------------------  
ID:          \${event_definition_id  
Type:        \${event_definition_type}  
Title:       \${event_definition_title}  
Description: \${event_definition_description}  
--- [Event] --------------------------------------  
Event:                \${event}  
--- [Event Detail] -------------------------------  
Timestamp:            \${event.timestamp}  
Message:              \${event.message}  
Source:               \${event.source}  
Key:                  \${event.key}  
Priority:             \${event.priority}  
Alert:                \${event.alert}  
Timestamp Processing: \${event.timestamp}  
Timerange Start:      \${event.timerange_start}  
Timerange End:        \${event.timerange_end}  
Fields:  
\${foreach event.fields field}  \${field.key}: \${field.value}  
\${end}  
\${if backlog}  
--- [Backlog] ------------------------------------  
Last messages accounting for this alert:  
\${foreach backlog message}  
\${message}  
\${end}  
\${end}
`

class TeamsNotificationForm extends React.Component {
  static propTypes = {
    config: PropTypes.object.isRequired,
    validation: PropTypes.object.isRequired,
    onChange: PropTypes.func.isRequired,
  };

  static defaultConfig = {
    webhook_url: '',
    graylog_url: '',
    color: '0076D7',
    message: DEFAULT_MSG,
    proxy_url: '',
  };

  propagateChange = (key, value) => {
    const { config, onChange } = this.props;
    const nextConfig = lodash.cloneDeep(config);
    nextConfig[key] = value;
    onChange(nextConfig);
  };

  handleChange = (event) => {
    const { name } = event.target;
    this.propagateChange(name, FormsUtils.getValueFromInput(event.target));
  };

  handleBodyTemplateChange = (nextValue) => {
    this.propagateChange('body_template', nextValue);
  };

  handleRecipientsChange = (key) => {
    return nextValue => this.propagateChange(key, nextValue === '' ? [] : nextValue.split(','));
  };

  render() {
    const { config, validation } = this.props;

    return (
      <React.Fragment>
        <Input id="notification-webhookUrl"
               name="webhook_url"
               label="Webhook URL"
               type="text"
               bsStyle={validation.errors.webhook_url ? 'error' : null}
               help={lodash.get(validation, 'errors.webhook_url[0]',
                   'Microsoft Teams Incoming Webhook URL')}
               value={config.webhook_url || ''}
               onChange={this.handleChange}
               required />
        <Input id="notification-graylogUrl"
               name="graylog_url"
               label="Graylog URL"
               type="text"
               bsStyle={validation.errors.graylog_url ? 'error' : null}
               help={lodash.get(validation, 'errors.graylog_url[0]',
                   'URL to be attached in notification')}
               value={config.graylog_url || ''}
               onChange={this.handleChange}/>
        <Input id="notification-color"
               name="color"
               label="Color"
               type="text"
               bsStyle={validation.errors.color ? 'error' : null}
               help={lodash.get(validation, 'errors.color[0]', 'Color code')}
               value={config.color || ''}
               onChange={this.handleChange}/>
        <Input id="notification-message"
               name="message"
               label="Message"
               type="textarea"
               bsStyle={validation.errors.message ? 'error' : null}
               help={lodash.get(validation, 'errors.message[0]',
                   'Detail message supporting basic Markdown syntax')}
               value={config.message || ''}
               onChange={this.handleChange}/>
        <Input id="notification-proxyUrl"
               name="proxy_url"
               label="Proxy URL"
               type="text"
               bsStyle={validation.errors.proxy_url ? 'error' : null}
               help={lodash.get(validation, 'errors.proxy_url[0]',
                   'Proxy URL in the following format "http(s)://${HOST}:${PORT}"')}
               value={config.proxy_url || ''}
               onChange={this.handleChange}/>
      </React.Fragment>
    );
  }
}

export default TeamsNotificationForm;
