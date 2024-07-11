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
import lodash from "lodash";
import { Input } from "components/bootstrap";
import FormsUtils from "util/FormsUtils";

const DEFAULT_CARD_TITLE = "Graylog Event Notification is triggered";
const DEFAULT_MSG = `# --- [Event Definition] ---------------------------  
**ID:**                   \${event_definition_id}  
**Type:**                 \${event_definition_type}  
**Title:**                \${event_definition_title}  
**Description:**          \${event_definition_description}  
# --- [Event] --------------------------------------  
**Event:**                \${event}  
# --- [Event Detail] -------------------------------  
**Timestamp:**            \${event.timestamp}  
**Message:**              \${event.message}  
**Source:**               \${event.source}  
**Key:**                  \${event.key}  
**Priority:**             \${event.priority}  
**Alert:**                \${event.alert}  
**Timestamp Processing:** \${event.timestamp}  
**TimeRange Start:**      \${event.timerange_start}  
**TimeRange End:**        \${event.timerange_end}  
\${if event.fields}
**Fields:**  
\${foreach event.fields field}  \${field.key}: \${field.value}  
\${end}
\${end}
\${if backlog}
# --- [Backlog] ------------------------------------  
**Messages:**  
\${foreach backlog message}
\`\`\`
\${message}  
\`\`\`
\${end}
\${end}`;

class TeamsNotificationForm extends React.Component {
  static propTypes = {
    config: PropTypes.object.isRequired,
    validation: PropTypes.object.isRequired,
    onChange: PropTypes.func.isRequired
  };

  static defaultConfig = {
    webhook_url: "",
    graylog_url: "",
    color: "0076D7",
    card_title: DEFAULT_CARD_TITLE,
    message: DEFAULT_MSG,
    proxy_url: ""
  };

  propagateChange = (key, value) => {
    const { config, onChange } = this.props;
    const nextConfig = lodash.cloneDeep(config);
    nextConfig[key] = value;
    onChange(nextConfig);
  };

  handleChange = event => {
    const { name } = event.target;
    this.propagateChange(name, FormsUtils.getValueFromInput(event.target));
  };

  render() {
    const { config, validation } = this.props;
    return (
      <React.Fragment>
        <Input
          id="notification-webhook-url"
          name="webhook_url"
          label="Webhook URL"
          type="text"
          bsStyle={validation.errors.webhook_url ? "error" : null}
          help={lodash.get(
            validation,
            "errors.webhook_url[0]",
            "Microsoft Teams Incoming Webhook URL"
          )}
          value={config.webhook_url || ""}
          onChange={this.handleChange}
          required
        />
        <Input
          id="notification-graylog-url"
          name="graylog_url"
          label="Graylog URL"
          type="text"
          bsStyle={validation.errors.graylog_url ? "error" : null}
          help={lodash.get(
            validation,
            "errors.graylog_url[0]",
            "URL to be attached in notification - if no URL is filled, the button 'Open Graylog' will not be added"
          )}
          value={config.graylog_url || ""}
          onChange={this.handleChange}
        />
        <Input
          id="notification-color"
          name="color"
          label="Title color"
          type="text"
          bsStyle={validation.errors.color ? "error" : null}
          help={lodash.get(validation, "errors.color[0]", "Title Color - Good / Warning / Attention")}
          value={config.color || "Warning"}
          onChange={this.handleChange}
        />
        <Input
          id="notification-card-title"
          name="card_title"
          label="Card title"
          type="text"
          bsStyle={validation.errors.message ? "error" : null}
          help={lodash.get(
            validation,
            "errors.message[0]",
            "Card title supporting basic Markdown syntax"
          )}
          value={config.card_title || ""}
          rows={15}
          onChange={this.handleChange}
        />
        <Input
          id="notification-message"
          name="message"
          label="Card body (message)"
          type="textarea"
          bsStyle={validation.errors.message ? "error" : null}
          help={lodash.get(
            validation,
            "errors.message[0]",
            "Detail message supports basic Markdown syntax. Create dynamic content using JMTE syntax."
          )}
          value={config.message || ""}
          rows={15}
          onChange={this.handleChange}
        />
        <Input
          id="notification-proxy-url"
          name="proxy_url"
          label="Proxy URL"
          type="text"
          bsStyle={validation.errors.proxy_url ? "error" : null}
          help={lodash.get(
            validation,
            "errors.proxy_url[0]",
            'Proxy URL in the following format "http(s)://${HOST}:${PORT}"'
          )}
          value={config.proxy_url || ""}
          onChange={this.handleChange}
        />
      </React.Fragment>
    );
  }
}

export default TeamsNotificationForm;
