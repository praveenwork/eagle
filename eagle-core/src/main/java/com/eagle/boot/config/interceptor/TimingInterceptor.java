package com.eagle.boot.config.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class TimingInterceptor implements ClientHttpRequestInterceptor {
    private static Logger LOGGER = LoggerFactory.getLogger(TimingInterceptor.class.getName());

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        long startTime = System.currentTimeMillis();
        try {
            return execution.execute(request, body);
        } finally {
            long timeTaken = System.currentTimeMillis() - startTime;
            LOGGER.debug("Time taken [{}] ms to invoke URI: [{}], Method [{}], Headers [{}]",
                    timeTaken, request.getMethod(), request.getHeaders());
        }
    }
}
