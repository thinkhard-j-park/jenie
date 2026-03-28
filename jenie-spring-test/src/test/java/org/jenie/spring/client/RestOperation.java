package org.jenie.spring.client;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

public abstract class RestOperation {

	protected RestClient restClient;

	protected ObjectMapper objectMapper;

	public RestOperation(RestClient restClient) {
		this.restClient = restClient;
		this.objectMapper = createObjectMapper();
	}

	protected ObjectMapper createObjectMapper() {
		return JsonMapper.builder().enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY).build();
	}

	protected <T> T doGet(String path, Map<String, Object> uriVariables,
			LinkedMultiValueMap<String, String> queryParams, ParameterizedTypeReference<T> responseType) {

		var uri = UriComponentsBuilder.fromPath(path)
			.uriVariables(Optional.ofNullable(uriVariables).orElse(Collections.emptyMap()))
			.queryParams(queryParams)
			.build()
			.toUriString();

		return this.restClient.get().uri(uri).retrieve().body(responseType);

	}

	protected <T> T doPost(String path, Map<String, Object> uriVariables,
			LinkedMultiValueMap<String, String> queryParams, Object requestBody,
			ParameterizedTypeReference<T> responseType) {

		var uri = UriComponentsBuilder.fromPath(path)
			.uriVariables(Optional.ofNullable(uriVariables).orElse(Collections.emptyMap()))
			.queryParams(queryParams)
			.build()
			.toUriString();

		return this.restClient.post()
			.uri(uri)
			.contentType(MediaType.APPLICATION_JSON)
			.body(requestBody)
			.retrieve()
			.body(responseType);
	}

	protected <T> T doPut(String path, Map<String, Object> uriVariables,
			LinkedMultiValueMap<String, String> queryParams, Object requestBody,
			ParameterizedTypeReference<T> responseType) {

		var uri = UriComponentsBuilder.fromPath(path)
			.uriVariables(Optional.ofNullable(uriVariables).orElse(Collections.emptyMap()))
			.queryParams(queryParams)
			.build()
			.toUriString();

		return this.restClient.put()
			.uri(uri)
			.contentType(MediaType.APPLICATION_JSON)
			.body(requestBody)
			.retrieve()
			.body(responseType);
	}

	protected <T> T doDelete(String path, Map<String, Object> uriVariables,
			LinkedMultiValueMap<String, String> queryParams, ParameterizedTypeReference<T> responseType) {
		var uri = UriComponentsBuilder.fromPath(path)
			.uriVariables(Optional.ofNullable(uriVariables).orElse(Collections.emptyMap()))
			.queryParams(queryParams)
			.build()
			.toUriString();

		return this.restClient.delete().uri(uri).retrieve().body(responseType);
	}

	public <T> LinkedMultiValueMap<String, String> toQueryParam(T obj) {
		return this.objectMapper.convertValue(obj, new TypeReference<>() {
		});
	}

}
