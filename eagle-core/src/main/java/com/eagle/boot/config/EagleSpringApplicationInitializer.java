package com.eagle.boot.config;

import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.MapPropertySource;

import com.eagle.boot.EagleProperties;


@Configuration
@ComponentScan("com.eagle")
public class EagleSpringApplicationInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

	private final static Logger LOGGER = LoggerFactory.getLogger(EagleSpringApplicationInitializer.class.getName());
    private final static String SHOW_PROPERTY_OVERRIDES_PROPERTY = EagleProperties.CONFIG_PREFIX + ".show-property-overrides-on-startup";

	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		LOGGER.info("EagleSpringApplicationInitializer...");
		Boolean doShowPropertyOverrides = applicationContext.getEnvironment().getProperty(SHOW_PROPERTY_OVERRIDES_PROPERTY, Boolean.class);
		if ( doShowPropertyOverrides != null && doShowPropertyOverrides ) {
			logPropertyOverrides(applicationContext);
		}
	}
	
	/**
     * If invoked, this method will log all the default properties in the base that have being overriden.
     * <br>The property keys and both values are logged
     * @param applicationContext - the application context to check for overrides
     */
    private void logPropertyOverrides(ConfigurableApplicationContext applicationContext) {
        Map<String, PropertyOverride> mapOfOverrides = new TreeMap<>();
        // For now, show all the overrides that the service has done
        MapPropertySource appDefaultSource =
                (MapPropertySource) applicationContext.getEnvironment().getPropertySources().get(EagleProperties.APPLICATION_DEFAULT_PROPERTY_YML);
        for(String defaultKey : appDefaultSource.getPropertyNames()) {
            Object defaultValue = appDefaultSource.getProperty(defaultKey);
            Object valueFound = applicationContext.getEnvironment().getProperty(defaultKey);
            String defaultValueString = defaultValue != null ? defaultValue.toString() : "";
            String valueFoundString = valueFound != null ? valueFound.toString() : "";

            if ( !StringUtils.equals(defaultValueString, valueFoundString)) {
                // Looks like we have an override, so let's save it for now
                mapOfOverrides.put(defaultKey, new PropertyOverride(defaultValueString, valueFoundString));
            }
        }
        if ( mapOfOverrides.isEmpty()) {
            LOGGER.info("No overrides were detected for the eagl base properties");
        } else {
            StringBuilder sb = new StringBuilder("\n\n*** Detected the following overrides for the eagle base default properties\n");
            mapOfOverrides.entrySet()
                    .forEach(e -> sb.append("\n")
                            .append(e.getKey())
                            .append(" : [").append(e.getValue().defaultValue)
                            .append("] --> [")
                            .append(e.getValue().overrideValue).append("]"));
            LOGGER.info(sb.toString()+"\n\n");
        }
    }


    private static class PropertyOverride {
        private String defaultValue;
        private String overrideValue;

        PropertyOverride(String dv, String ov) {
            this.defaultValue = dv;
            this.overrideValue = ov;
        }
    }
	
}
