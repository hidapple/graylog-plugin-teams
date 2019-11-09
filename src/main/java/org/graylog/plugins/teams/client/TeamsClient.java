package org.graylog.plugins.teams.client;

import com.floreysoft.jmte.Engine;
import com.google.common.base.Strings;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Objects;
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
    if (Objects.isNull(url)) {
      throw new TeamsClientException("Teams webhook URL is invalid format. URL=" + config.webhookURL());
    }
    final RequestBody reqBody = RequestBody.create(MediaType.get("application/json"), createRequest(config, model).toJsonString());
    final Request req = new Request.Builder()
        .url(url)
        .post(reqBody)
        .build();
    LOG.debug("Request: " + req.toString());

    // Response
    try (final Response res = client.newCall(req).execute()) {
      if (!res.isSuccessful()) {
        LOG.debug(res.toString());
        throw new TeamsClientException("Teams webhook returned unexpected response status. HTTP Status=" + res.code());
      }
    } catch (final IOException ex) {
      throw new TeamsClientException("Failed to send POST request to the Teams webhook.", ex);
    }
  }

  private TeamsMessageCard createRequest(final TeamsEventNotificationConfig config, final Map<String, Object> model) {
    return new TeamsMessageCard(
        config.color(),
        "Graylog Event Notification is triggered",
        "Event: " + model.get("event_definition_title"),
        buildMessage(config, model),
        config.graylogURL()
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

  private String buildMessage(final TeamsEventNotificationConfig config, final Map<String, Object> model) {
    final String template;
    if (Strings.isNullOrEmpty(config.message())) {
      template = TeamsEventNotificationConfig.DEFAULT_MESSAGE;
    } else {
      template = config.message();
    }
    return templateEngine.transform(template, model);
  }
}
