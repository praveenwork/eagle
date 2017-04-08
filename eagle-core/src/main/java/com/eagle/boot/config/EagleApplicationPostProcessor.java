package com.eagle.boot.config;

import java.io.IOException;
import java.io.PrintStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;


@Order(-500)
public class EagleApplicationPostProcessor implements EnvironmentPostProcessor {

	private final static Logger LOGGER = LoggerFactory.getLogger(EagleApplicationPostProcessor.class);

	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
		LOGGER.info("Loading Properties...");
		// Set the default behavior of the app, before the context is loaded
		loadYmlFile(environment, "eagle-spring-boot-base-defaults.yml", null);
		application.setBannerMode(Banner.Mode.CONSOLE);
        application.setBanner(EagleApplicationPostProcessor::printBanner);
        application.setHeadless(true);
        application.setWebEnvironment(true);
        LOGGER.info("Successfully configured the Spring Boot Application before the context starts");
	}

	// ---------Helpers-----------
	private PropertySource<?> loadYmlFile(ConfigurableEnvironment environment, String resourceName, String profile) {
		try {
			YamlPropertySourceLoader ymlPropertyLoader = new YamlPropertySourceLoader();
			Resource resource = new ClassPathResource(resourceName);
			PropertySource<?> propertySource = ymlPropertyLoader.load(resourceName, resource, profile);
			if (propertySource != null) {
				environment.getPropertySources().addLast(propertySource);
			}
			return propertySource;
		} catch (IOException e) {
			throw new IllegalStateException("Could not read the yml classpath resource ["+resourceName+"]", e);
		}
	}
	private static void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {
        out.print(System.lineSeparator()+EAGLE_BANNER+System.lineSeparator()+System.lineSeparator());
    }
	
	private static final String EAGLE_BANNER = 
			" _____            _        _____             _            \n"
			+ "| ____|__ _  __ _| | ___  | ____|_ __   __ _(_)_ __   ___ \n"
			+ "|  _| / _` |/ _` | |/ _ \\ |  _| | '_ \\ / _` | | '_ \\ / _ \\ \n"
			+ "| |__| (_| | (_| | |  __/ | |___| | | | (_| | | | | |  __/\n"
			+ "|_____\\__,_|\\__, |_|\\___| |_____|_| |_|\\__, |_|_| |_|\\___|\n"
			+ "            |___/                      |___/              \n";
}
