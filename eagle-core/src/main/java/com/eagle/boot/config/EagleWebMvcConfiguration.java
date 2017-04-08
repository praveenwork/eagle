package com.eagle.boot.config;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.eagle.boot.EagleProperties;

@AutoConfigureAfter(value = WebMvcAutoConfiguration.class)
@ConditionalOnProperty(value = EagleProperties.WEBMVC_ENABLED, havingValue = "true")
@Configuration
public class EagleWebMvcConfiguration extends WebMvcConfigurerAdapter implements InitializingBean {

    private final static Logger LOGGER = LoggerFactory.getLogger(EagleWebMvcConfiguration.class.getName());

    @NotNull
    @Value("${"+EagleProperties.API_DOCUMENTATION_ENABLED+"}")
    private Boolean apiDocumentationEnabled;
    @NotNull
    @Value("${"+EagleProperties.INTERNALS_RESOURCE_ENABLED +"}")
    private Boolean internalsResourceEnabled;
    @NotBlank
    @Value("${"+EagleProperties.API_DOCUMENTATION_UI_RESOURCE+"}")
    private String apiDocumentationUIResource;
    @NotBlank
    @Value("${"+EagleProperties.API_DOCUMENTATION_UI_REDIRECT_RESOURCE+"}")
    private String apiDocumentationUIRedirectResource;
   
    @Override
    public void afterPropertiesSet() {
        LOGGER.info("The Eagle WebMVC Configuration has being configured and applied");
    }

    @Bean //this could be provided via auto-configuration
    protected MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }

    /**
     * Depending on what configuration we've enabled thus far, we'll create resource handlers for each,
     * such as api-documentation, internals resources, static and so forth.
     * @param registry - the registry to use to register new resource handlers
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        /*
         * Check if we should add various resource handlers for documentations - gotta add it here
         * since between springfox/swagger/spring-boot and webmvc, oh jeez, so many settings
         */
        if (internalsResourceEnabled) {
            if ( apiDocumentationEnabled) {
                // Set up the static resource handlers for swagger
                String pattern = "/resources/**";
                if (!registry.hasMappingForPattern(pattern)) {
                    registry.addResourceHandler(pattern).addResourceLocations("/resources/");
                }
                pattern = apiDocumentationUIResource;
                if (!registry.hasMappingForPattern(pattern)) {
                    registry.addResourceHandler(pattern).addResourceLocations("classpath:/META-INF/resources/");
                }
                pattern = "/webjars/**";
                if (!registry.hasMappingForPattern(pattern)) {
                    registry.addResourceHandler(pattern).addResourceLocations("classpath:/META-INF/resources/webjars/");
                }
            }
        } else {
            // We don't want the static resource to be found, so reroute it to "nothing" - get a 404
            registry.addResourceHandler(apiDocumentationUIResource).addResourceLocations("");
        }
    }

    /**
     * Register views that should be used for certain resources - for this, we register a swagger view
     * for "/service/internals/docs"
     * @param registry - The registry to use to register new resource views
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        if (internalsResourceEnabled) {
            if ( apiDocumentationEnabled ) {
                registry.addRedirectViewController(apiDocumentationUIRedirectResource, apiDocumentationUIResource);
            }
        }
    }

}
