package org.graylog.plugins.teams.client;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import org.apache.commons.lang.StringUtils;
import org.graylog2.plugin.alarms.callbacks.AlarmCallbackException;
import org.graylog2.plugin.configuration.Configuration;

public class TeamsClient {

  private final String webhookURL;

  private final String proxyURL;

  public TeamsClient(Configuration config) {
    this.webhookURL = config.getString("webhook_url");
    this.proxyURL = config.getString("proxy");
  }

  public void send(TeamsWebhookRequest request) throws AlarmCallbackException {
    URL url;
    try {
      url = new URL(webhookURL);
    } catch (MalformedURLException ex) {
      throw new AlarmCallbackException(
          MessageFormat.format("Teams webhook URL is invalid format. URL={}", webhookURL), ex);
    }

    // Configure connection
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
      throw new AlarmCallbackException(
          MessageFormat.format("Proxy URI is invalid format. URI={}", proxyURL), ex);
    } catch (IOException ex) {
      throw new AlarmCallbackException(
          MessageFormat.format("Failed to open connection to the Teams webhook. URL={}", webhookURL), ex);
    }

    // Request body
    try (OutputStreamWriter w = new OutputStreamWriter(con.getOutputStream())) {
      w.write(request.toJsonString());
      w.flush();

      // TODO: Return HTTP response and leave handing response to the caller.
      if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
        throw new AlarmCallbackException("Teams webhook returned unexpected response status");
      }
    } catch (IOException ex) {
      throw new AlarmCallbackException("Failed to POST the request to the Teams webhook.", ex);
    }

    // TODO: Response handling
  }
}
