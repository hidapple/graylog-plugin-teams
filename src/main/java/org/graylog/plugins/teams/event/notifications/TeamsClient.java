package org.graylog.plugins.teams.event.notifications;

import com.floreysoft.jmte.Engine;
import com.google.common.base.Strings;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import javax.inject.Inject;
import org.apache.commons.io.IOUtils;
import org.graylog.plugins.teams.client.TeamsMessageCard;
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
    final URL webhook;
    try {
      webhook = new URL(Objects.requireNonNull(config.webhookURL()));
    } catch (final MalformedURLException e) {
      throw new TeamsClientException("Teams webhook URL is invalid format. URL=" + config.webhookURL(), e);
    }

    final HttpURLConnection conn;
    try {
      if (Strings.isNullOrEmpty(config.proxyURL())) {
        conn = (HttpURLConnection) webhook.openConnection();
      } else {
        conn = (HttpURLConnection) webhook.openConnection(buildProxy(config.proxyURL()));
      }
    } catch (final IOException e) {
      throw new TeamsClientException("Failed to open connection to the Teams webhook", e);
    }

    try (final OutputStreamWriter w = new OutputStreamWriter(conn.getOutputStream())) {
      w.write(createRequest(config, model).toJsonString());
      w.flush();

      if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
        if (LOG.isDebugEnabled()) {
          try (final InputStream in = conn.getInputStream()) {
            final String res = IOUtils.toString(in, StandardCharsets.UTF_8);
            LOG.debug("HTTP response body={}", res);
          } catch (final IOException ex) {
            LOG.debug("Failed to get HTTP response body", ex);
          }
        }
        throw new TeamsClientException("Teams webhook returned unexpected response status. HTTP Status=" + conn.getResponseCode());
      }
    } catch (final IOException e) {
      throw new TeamsClientException("Failed to send POST request to the Teams webhook.", e);
    }
  }

  private TeamsMessageCard createRequest(final TeamsEventNotificationConfig config, final Map<String, Object> model) {
    return new TeamsMessageCard(
        config.color(),
        "Title",
        "Alert for Graylog stream",
        buildMessage(config, model),
        config.graylogURL()
    );
  }

  private Proxy buildProxy(final String proxyURL) {
    try {
      final URI uri = new URI(proxyURL);
      return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(uri.getHost(), uri.getPort()));
    } catch (final URISyntaxException e) {
      // TODO: Make custom exception
      throw new TeamsClientException("TODO", e);
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
