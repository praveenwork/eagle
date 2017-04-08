package com.eagle.boot.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextListener;

import com.eagle.boot.EagleProperties;

@ConditionalOnProperty(value = EagleProperties.BASE_CONFIGURATION_ENABLED, havingValue = "true")
@Configuration
public class EagleBaseConfiguration implements InitializingBean {

    private final static Logger LOGGER = LoggerFactory.getLogger(EagleBaseConfiguration.class.getName());
    
    @Override
    public void afterPropertiesSet() {
        LOGGER.info("Eagle base configuration has being configured and applied");
    }

    @Bean
    public EagleRestTemplate eagleRestTemplate() {
        return new EagleRestTemplate();
    }
    
    @Bean
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }
}
