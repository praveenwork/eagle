package com.eagle.boot.config.property;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.eagle.boot.EagleProperties;

@Configuration
@ConfigurationProperties(prefix = EagleLoggingProperties.PREFIX)
public class EagleLoggingProperties {

    final static String PREFIX = EagleProperties.CONFIG_PREFIX+".logging";

    @NotBlank(message = "The "+ PREFIX +".name property must be set explicitly")
    private String name;
    @NotBlank(message = "The "+ PREFIX +".directory property must be set explicitly")
    private String directory;
    @NotNull(message = "The "+ PREFIX +".correlation-id-enabled property must be set")
    private Boolean correlationIdEnabled = true;

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getCorrelationIdEnabled() {
        return correlationIdEnabled;
    }

    public void setCorrelationIdEnabled(Boolean correlationIdEnabled) {
        this.correlationIdEnabled = correlationIdEnabled;
    }

    @Override
    public String toString() {
        return "EagleRequiredLoggingProperties{" +
                "name='" + name + '\'' +
                ", correlationIdEnabled='" + correlationIdEnabled + '\'' +
                '}';
    }
}
