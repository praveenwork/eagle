package com.eagle.boot.config;

import java.util.List;

import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.eagle.boot.config.interceptor.TimingInterceptor;

public class EagleRestTemplate extends RestTemplate {

	public EagleRestTemplate() {
		super();
		addInterceptors();
	}

	private void addInterceptors() {
		super.getInterceptors().add(new TimingInterceptor()); // capture timings
	}

	public EagleRestTemplate(ClientHttpRequestFactory requestFactory) {
		super(requestFactory);
		addInterceptors();
	}

	public EagleRestTemplate(List<HttpMessageConverter<?>> messageConverters) {
		super(messageConverters);
		addInterceptors();
	}
}
