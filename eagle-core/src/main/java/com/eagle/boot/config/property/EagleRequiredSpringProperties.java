package com.eagle.boot.config.property;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Configuration
@ConfigurationProperties(prefix = EagleRequiredSpringProperties.PREFIX_SPRING)
public class EagleRequiredSpringProperties {

    final static String PREFIX_SPRING = "spring";
    private final static String PREFIX_APPLICATION = PREFIX_SPRING + ".application";

    @Valid
    @NotNull(message = "No "+PREFIX_APPLICATION+" properties were set")
    private ApplicationProperties application;

    public static class ApplicationProperties {

        @NotBlank(message = "There is no explicit property set for "+PREFIX_APPLICATION+".name")
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public ApplicationProperties getApplication() {
        return application;
    }

    public void setApplication(ApplicationProperties application) {
        this.application = application;
    }
}
