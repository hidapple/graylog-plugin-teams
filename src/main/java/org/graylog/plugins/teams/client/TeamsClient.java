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
package org.graylog.plugins.teams.client;

import com.floreysoft.jmte.Engine;
import com.google.common.base.Strings;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.graylog.plugins.teams.event.notifications.TeamsEventNotificationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TeamsClient {

  private static final Logger LOG = LoggerFactory.getLogger(TeamsClient.class);

  private final Engine templateEngine;

  @Inject
  public TeamsClient(final Engine engine) {
    this.templateEngine = engine;
  }

  public void postMessageCard(final TeamsEventNotificationConfig config, final Map<String, Object> model) {
    // Create HTTP client
    final OkHttpClient client;
    if (Strings.isNullOrEmpty(config.proxyURL())) {
      client = new OkHttpClient();
    } else {
      client = new OkHttpClient.Builder().proxy(buildProxy(config.proxyURL())).build();
    }

    // Create request
    final HttpUrl url = HttpUrl.parse(config.webhookURL());
    if (url == null) {
      throw new TeamsClientException("Teams webhook URL is invalid format. URL=" + config.webhookURL());
    }
    final RequestBody reqBody = RequestBody.create(MediaType.get("application/json"), createRequest(config, model).toJsonString());
    final Request req = new Request.Builder()
        .url(url)
        .post(reqBody)
        .build();
    LOG.debug("Request: {}", req);

    // Response
    try (final Response res = client.newCall(req).execute()) {
      if (!res.isSuccessful()) {
        LOG.debug("Failed response from Teams: {}", res);
        throw new TeamsClientException("Teams webhook returned unexpected response status. HTTP Status=" + res.code());
      }
    } catch (final IOException ex) {
      throw new TeamsClientException("Failed to send POST request to the Teams webhook.", ex);
    }
  }

  private TeamsMessageCard createRequest(final TeamsEventNotificationConfig config, final Map<String, Object> model) {
    final Object backlog = model.get("backlog");
    String graylogMsgUrl = null;
    if (!Strings.isNullOrEmpty(config.graylogURL()) && backlog instanceof List) {
      final Map<String, Object> firstMsgSummary = ((List<Map<String, Object>>) backlog).stream().findFirst().orElse(null);
      if (firstMsgSummary != null) {
        final String graylogUrl = config.graylogURL().endsWith("/") ? config.graylogURL() : config.graylogURL() + "/";
        graylogMsgUrl = graylogUrl + "messages/" + firstMsgSummary.get("index") + "/" + firstMsgSummary.get("id");
      }
    }

    return new TeamsMessageCard(
        buildTemplateText(config.cardTitle(), TeamsEventNotificationConfig.DEFAULT_CARD_TITLE, model),
        config.color(),
        buildTemplateText(config.message(), TeamsEventNotificationConfig.DEFAULT_MESSAGE, model),
        graylogMsgUrl
    );
  }

  private Proxy buildProxy(final String proxyURL) {
    try {
      final URI uri = new URI(proxyURL);
      return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(uri.getHost(), uri.getPort()));
    } catch (final URISyntaxException e) {
      throw new TeamsClientException("Proxy URL is invalid format. Proxy URL=" + proxyURL, e);
    }
  }

  private String buildTemplateText(final String textTemplate, final String defaultText, final Map<String, Object> model) {
    final String template;
    if (Strings.isNullOrEmpty(textTemplate)) {
      template = defaultText;
    } else {
      template = textTemplate;
    }
    return templateEngine.transform(template, model);
  }
}
