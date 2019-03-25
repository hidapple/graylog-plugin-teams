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
import java.text.MessageFormat;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.graylog.plugins.teams.alerts.TeamsNotificationConfig;
import org.graylog2.plugin.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TeamsClient {

  private static final Logger LOG = LoggerFactory.getLogger(TeamsClient.class);

  private final String webhookURL;
  private final String proxyURL;

  public TeamsClient(Configuration config) {
    this.webhookURL = config.getString(TeamsNotificationConfig.WEBHOOK_URL);
    this.proxyURL = config.getString(TeamsNotificationConfig.PROXY);
  }

  public void postMessageCard(TeamsMessageCard request) throws TeamsClientException {
    URL url;
    try {
      url = new URL(webhookURL);
    } catch (MalformedURLException ex) {
      throw new TeamsClientException(
          MessageFormat.format("Teams webhook URL is invalid format. URL={}", webhookURL), ex);
    }

    HttpURLConnection con;
    try {
      if (StringUtils.isEmpty(proxyURL)) {
        con = (HttpURLConnection) url.openConnection();
      } else {
        URI proxyURI = new URI(proxyURL);
        Proxy proxy = new Proxy(Type.HTTP, new InetSocketAddress(proxyURI.getHost(), proxyURI.getPort()));
        con = (HttpURLConnection) url.openConnection(proxy);
      }
      con.setRequestMethod("POST");
      con.setRequestProperty("Content-Type", "application/json");
      con.setDoOutput(true);
    } catch (URISyntaxException ex) {
      throw new TeamsClientException(MessageFormat.format("Proxy URI is invalid format. URI={}", proxyURL), ex);
    } catch (IOException ex) {
      throw new TeamsClientException(MessageFormat.format("Failed to open connection to the Teams webhook. URL={}", webhookURL), ex);
    }

    try (OutputStreamWriter w = new OutputStreamWriter(con.getOutputStream())) {
      LOG.debug("HTTP request body={}", request.toJsonString());
      w.write(request.toJsonString());
      w.flush();

      if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
        if (LOG.isDebugEnabled()) {
          try (InputStream in = con.getInputStream()) {
            String res = IOUtils.toString(in, StandardCharsets.UTF_8);
            LOG.debug("HTTP response body={}", res);
          } catch (IOException ex) {
            LOG.debug("Failed to get HTTP response body", ex);
          }
        }
        throw new TeamsClientException("Teams webhook returned unexpected response status. HTTP Status=" + con.getResponseCode());
      }
    } catch (IOException ex) {
      throw new TeamsClientException("Failed to send POST request to the Teams webhook.", ex);
    }
  }
}
