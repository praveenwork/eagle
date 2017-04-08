package com.eagle.boot.config;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.eagle.boot.EagleProperties;
import com.eagle.boot.config.property.EagleRequiredServerProperties;
import com.google.common.base.Predicate;

import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Import(BeanValidatorPluginsConfiguration.class)
@Configuration
@EnableSwagger2
public class EagleDocumentationConfiguration implements InitializingBean {

    private final static Logger LOGGER = LoggerFactory.getLogger(EagleDocumentationConfiguration.class.getName());

    @Autowired
    private EagleRequiredServerProperties requiredServerProperties;

    @Value("${"+ EagleProperties.API_DOCUMENTATION_ENABLED+"}")
    private boolean apiDocumentationEnabled;
    
    @Value("${"+ EagleProperties.INTERNALS_RESOURCE_ENABLED +"}")
    private boolean isInternalsResourceEnabled;
    
    @Value("${"+EagleProperties.APPLICATION_IMPLEMENTATION_VERSION+":Unknown}")
    private String applicationImplementationVersion;
    @NotBlank
    @Value("${"+EagleProperties.API_DOCUMENTATION_RESOURCES_BASE_PACKAGE_NAME+"}")
    private String apiResourceBasePackageName;
    @NotBlank
    @Value("${"+EagleProperties.API_DOCUMENTATION_RESOURCE_PATH_REGEX+"}")
    private String apiResourcePathRegex;

    @Override
    public void afterPropertiesSet() {
        LOGGER.info("The Eagle Documentation Configuration has being configured and applied");
    }

    @Bean
    public Docket apiDocumentation() {
        Docket docket = new Docket(DocumentationType.SWAGGER_2);
        if ( apiDocumentationEnabled && isInternalsResourceEnabled) {
            Predicate<RequestHandler> apiSelectorPredicate = !StringUtils.isBlank(apiResourceBasePackageName)
                    ? RequestHandlerSelectors.basePackage(apiResourceBasePackageName) : RequestHandlerSelectors.any();
            Predicate<String> apiPathSelector = !StringUtils.isBlank(apiResourcePathRegex)
                    ? PathSelectors.regex(apiResourcePathRegex) : PathSelectors.any();

            docket
                .select()
                .apis(apiSelectorPredicate)
                .paths(apiPathSelector)
                .build()
                .useDefaultResponseMessages(false) // Don't generate default responses
                .pathMapping("/") // TODO: allow for overrides
                .enable(apiDocumentationEnabled && isInternalsResourceEnabled) // Enable only when both are enabled
                .apiInfo(getApiInfo());
        } else {
            return null; //docket.enable(false);
        }
        return docket;
    }

    /**
     * Simple helper to get some basic information about this API automatically
     * @return - the API info to display on the html view
     */
    private ApiInfo getApiInfo() {
        String contextRoot = StringUtils.capitalize(StringUtils.remove(requiredServerProperties.getContextPath(), "/"));
        return new ApiInfoBuilder()
                .title(contextRoot+" ("+applicationImplementationVersion+") Service API Documentation")
                .description("Auto-generated SpringFox/Swagger '"+contextRoot+"' API Information")
                .version(applicationImplementationVersion)
                .build();
    }
}

