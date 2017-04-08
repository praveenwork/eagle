package com.eagle.boot.config.property;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotNull;

@Configuration
@ConfigurationProperties(prefix = EagleRequiredServerProperties.SERVER_PREFIX)
public class EagleRequiredServerProperties {

    static final String SERVER_PREFIX = "server";

    @NotBlank(message = "The property server.contextPath must be set for this application's context root")
    private String contextPath;
    @NotNull(message = "The property server.port must be set explicitly")
    private Integer port; // Can be negative

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "EagleRequiredServerProperties{" +
                "port=" + port +
                ", contextPath='" + contextPath + '\'' +
                '}';
    }
}
