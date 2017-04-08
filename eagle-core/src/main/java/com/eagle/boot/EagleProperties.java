package com.eagle.boot;

public final class EagleProperties {
    public final static String APPLICATION_DEFAULT_PROPERTY_YML = "eagle-spring-boot-base-defaults.yml";
    public final static String APPLICATION_NAME = "spring.application.name";
    public final static String CONFIG_PREFIX = "eagle.config";
    public final static String SPRING_BOOT_BASE_IMPLEMENTATION_VERSION = "eagle.spring-boot.implementation-version";

    public final static String LOG_FILE_NAME = CONFIG_PREFIX+".logging.name";
    public final static String LOG_FILE_DIRECTORY = CONFIG_PREFIX+".logging.directory";
    
    public final static String BASE_CONFIGURATION_ENABLED = CONFIG_PREFIX+".base-configuration-enabled";
    public final static String JETTY_SERVER_ENABLED = CONFIG_PREFIX+".jetty-server.enabled";
    public final static String EVENTS_CONFIGURATION_ENABLED = CONFIG_PREFIX+".events.configuration-enabled";
    public final static String WEBMVC_ENABLED = CONFIG_PREFIX+".web-mvc.enabled";
    
    public final static String ERROR_ENDPOINT_ENABLED = CONFIG_PREFIX+".error-endpoint.enabled";

    

    
    
    public final static String API_DOCUMENTATION_ENABLED = CONFIG_PREFIX+".api-documentation.enabled";
    public final static String INTERNALS_RESOURCE_ENABLED = CONFIG_PREFIX+".internals-resource.enabled";
    
    public final static String THROTTLING_ENABLED = CONFIG_PREFIX+".throttling-enabled";
    public final static String INTERNALS_RESOURCE_NAME = CONFIG_PREFIX+".internals-resource.name";
    public final static String INTERNALS_RESOURCE_PATTERN = CONFIG_PREFIX+".internals-resource.pattern";
    public final static String API_DOCUMENTATION_RESOURCES_BASE_PACKAGE_NAME = CONFIG_PREFIX+".api-documentation.resource-base-package";
    public final static String API_DOCUMENTATION_ALLOWED_RESOURCES = CONFIG_PREFIX+".api-documentation.allowed-resources";
    public final static String API_DOCUMENTATION_UI_RESOURCE = CONFIG_PREFIX+".api-documentation.ui-resource";
    public final static String API_DOCUMENTATION_UI_REDIRECT_RESOURCE = CONFIG_PREFIX+".api-documentation.ui-redirect-resource";
    public final static String API_DOCUMENTATION_RESOURCE_PATH_REGEX = CONFIG_PREFIX+".api-documentation.resource-path-regex";
    
//    public final static String HEALTH_CHECK_ENABLED = CONFIG_PREFIX+".health-check.enabled";
//    public final static String HEALTH_CHECK_FILTER_URL = CONFIG_PREFIX+".health-check.filter_url";
//    public final static String HEALTH_CHECK_STATISTICS_FILTER_URL = CONFIG_PREFIX+".health-check.statistics-filter_url";
    
    public final static String APPLICATION_IMPLEMENTATION_VERSION = CONFIG_PREFIX + ".application-implementation-version";


}
