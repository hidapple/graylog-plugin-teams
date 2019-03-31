package org.graylog.plugins.teams.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.graylog.plugins.teams.alerts.TeamsNotificationConfig;
import org.graylog2.plugin.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TeamsClient {

  private static final Logger LOG = LoggerFactory.getLogger(TeamsClient.class);

  private final URL webhook;
  private Proxy proxy;

  public TeamsClient(Configuration config) throws TeamsClientException {
    final String webhookStr = config.getString(TeamsNotificationConfig.WEBHOOK_URL);
    try {
     this.webhook = new URL(Objects.requireNonNull(webhookStr));
    } catch (MalformedURLException ex) {
      throw new TeamsClientException("Teams webhook URL is invalid format. URL=" + webhookStr, ex);
    }

    final String proxyStr = config.getString(TeamsNotificationConfig.PROXY);
    if (!StringUtils.isEmpty(proxyStr)) {
      try {
        URI uri = new URI(proxyStr);
        this.proxy = new Proxy(Type.HTTP, new InetSocketAddress(uri.getHost(), uri.getPort()));
      } catch (URISyntaxException ex) {
        throw new TeamsClientException("Proxy URI is invalid format. URI=" + proxyStr, ex);
      }
    }
  }

  public void postMessageCard(TeamsMessageCard messageCard) throws TeamsClientException {
    HttpURLConnection conn;
    try {
      if (Objects.isNull(this.proxy)) {
        conn = (HttpURLConnection) this.webhook.openConnection();
      } else {
        conn = (HttpURLConnection) this.webhook.openConnection(this.proxy);
      }
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Content-Type", "application/json");
      conn.setDoOutput(true);
    } catch (IOException ex) {
      throw new TeamsClientException("Failed to open connection to the Teams webhook", ex);
    }

    try (OutputStreamWriter w = new OutputStreamWriter(conn.getOutputStream())) {
      w.write(messageCard.toJsonString());
      w.flush();

      if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
        if (LOG.isDebugEnabled()) {
          try (InputStream in = conn.getInputStream()) {
            String res = IOUtils.toString(in, StandardCharsets.UTF_8);
            LOG.debug("HTTP response body={}", res);
          } catch (IOException ex) {
            LOG.debug("Failed to get HTTP response body", ex);
          }
        }
        throw new TeamsClientException("Teams webhook returned unexpected response status. HTTP Status=" + conn.getResponseCode());
      }
    } catch (IOException ex) {
      throw new TeamsClientException("Failed to send POST request to the Teams webhook.", ex);
    }
  }
}
