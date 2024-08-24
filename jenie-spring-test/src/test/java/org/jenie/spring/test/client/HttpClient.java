package org.jenie.spring.test.client;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.DefaultConnectionKeepAliveStrategy;
import org.apache.hc.client5.http.impl.DefaultHttpRequestRetryStrategy;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

public final class HttpClient {

	private static final Logger logger = LoggerFactory.getLogger(HttpClient.class);

	private HttpClient() {
	}

	private static HttpComponentsClientHttpRequestFactory requestFactory(String alias) {
		RequestConfig config = RequestConfig.custom()
			.setConnectionRequestTimeout(Timeout.ofSeconds(5))
			.setResponseTimeout(Timeout.ofSeconds(5))
			.build();

		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		cm.setMaxTotal(200);
		cm.setDefaultMaxPerRoute(200);
		cm.setDefaultSocketConfig(SocketConfig.DEFAULT);

		CloseableHttpClient client = HttpClientBuilder.create()
			.setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy())
			.setRetryStrategy(new DefaultHttpRequestRetryStrategy(1, TimeValue.ofMilliseconds(1000)))
			.addRequestInterceptorFirst((httpRequest, entityDetails, httpContext) -> {
				httpContext.setAttribute("ts", System.currentTimeMillis());
				logger.info("[{}] Request -> {}", alias, httpRequest);
			})
			.setDefaultRequestConfig(config)
			.setConnectionManager(cm)
			.evictExpiredConnections()
			.evictIdleConnections(TimeValue.ofSeconds(30))
			.build();

		return new HttpComponentsClientHttpRequestFactory(client);
	}

	public static RestClient restClient(String alias, String baseUrl) {
		var agent = baseUrl.substring(0, "http://".length());
		return RestClient.builder()
			.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.defaultHeader(HttpHeaders.USER_AGENT, agent + "@" + alias)
			.requestFactory(requestFactory(alias))
			.baseUrl(baseUrl)
			.build();

	}

}
