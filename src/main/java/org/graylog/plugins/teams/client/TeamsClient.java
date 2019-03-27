package org.graylog.plugins.teams.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.graylog.plugins.teams.alerts.TeamsNotificationConfig;
import org.graylog2.plugin.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TeamsClient {

  private static final Logger LOG = LoggerFactory.getLogger(TeamsClient.class);

  private final String webhookURL;
  private final OkHttpClient client;

  public TeamsClient(Configuration config) throws TeamsClientException {
    this.webhookURL = config.getString(TeamsNotificationConfig.WEBHOOK_URL);

    String proxyURL = config.getString(TeamsNotificationConfig.PROXY);
    if (StringUtils.isEmpty(proxyURL)) {
      this.client = new OkHttpClient();
    } else {
      URI uri;
      try {
        uri = new URI(proxyURL);
      } catch (URISyntaxException ex) {
        throw new TeamsClientException("Proxy URI is invalid format. URI=" + proxyURL, ex);
      }
      Proxy proxy = new Proxy(Type.HTTP, new InetSocketAddress(uri.getHost(), uri.getPort()));
      this.client = new OkHttpClient.Builder().proxy(proxy).build();
    }
  }

  public void postMessageCard(TeamsMessageCard messageCard) throws TeamsClientException {
    HttpUrl url = HttpUrl.parse(webhookURL);
    if (Objects.isNull(url)) {
      throw new TeamsClientException("Teams webhook URL is invalid format. URL=" + webhookURL);
    }
    RequestBody body = RequestBody.create(MediaType.get("application/json"), messageCard.toJsonString());
    Request req = new Request.Builder()
        .url(url)
        .post(body)
        .build();
    LOG.debug(req.toString());

    try (Response res = client.newCall(req).execute()) {
      if (!res.isSuccessful()) {
        LOG.debug(res.toString());
        throw new TeamsClientException("Teams webhook returned unexpected response status. HTTP Status=" + res.code());
      }
    } catch (IOException ex) {
      throw new TeamsClientException("Failed to send POST request to the Teams webhook.", ex);
    }
  }
}
