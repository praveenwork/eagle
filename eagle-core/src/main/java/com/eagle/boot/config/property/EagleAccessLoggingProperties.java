package com.eagle.boot.config.property;

import java.io.File;
import java.util.TimeZone;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.eagle.boot.EagleProperties;

@Configuration
@ConditionalOnProperty(value = EagleAccessLoggingProperties.PREFIX+".enabled", havingValue = "true")
@ConfigurationProperties(prefix = EagleAccessLoggingProperties.PREFIX)
public class EagleAccessLoggingProperties implements InitializingBean {

    public final static String PREFIX = EagleProperties.CONFIG_PREFIX + ".logging.access-log";
    private final static Logger LOGGER = LoggerFactory.getLogger(EagleAccessLoggingProperties.class.getName());

    private String logName; // Can be null for now
    @NotNull(message = "There is no "+PREFIX+".enabled property set")
    private Boolean enabled;
    @NotNull(message = "There is no "+PREFIX+".append-enabled property set")
    private Boolean appendEnabled;
    @NotNull(message = "There is no "+PREFIX+".extended-info-enabled property set")
    private Boolean extendedInfoEnabled;
    @NotBlank(message = "There is no "+PREFIX+".timezone property set")
    private String timezone = TimeZone.getDefault().getID();
    @NotNull(message = "There is no "+PREFIX+".retention-days property set")
    @Min(value = 0, message = "The "+PREFIX+".retention-days property is not valid")
    private Integer retentionDays;
    @NotBlank(message = "There is no "+PREFIX+".date-time-format property set")
    private String dateTimeFormat;
    @NotBlank(message = "There is no "+PREFIX+".postfix property set")
    private String postfix;

    @Valid
    @Autowired
    private EagleLoggingProperties loggingProperties;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (StringUtils.isBlank(logName)) {
            // set it to the other known properties
            logName = loggingProperties.getDirectory() + File.separator
                    + loggingProperties.getName() + postfix;
        }
        LOGGER.info("The access log configuration is setup as follows: "+this.toString());
    }

    public String getLogName() {
        return logName;
    }

    public void setLogName(String logName) {
        this.logName = logName;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getAppendEnabled() {
        return appendEnabled;
    }

    public void setAppendEnabled(Boolean appendEnabled) {
        this.appendEnabled = appendEnabled;
    }

    public Boolean getExtendedInfoEnabled() {
        return extendedInfoEnabled;
    }

    public void setExtendedInfoEnabled(Boolean extendedInfoEnabled) {
        this.extendedInfoEnabled = extendedInfoEnabled;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public Integer getRetentionDays() {
        return retentionDays;
    }

    public void setRetentionDays(Integer retentionDays) {
        this.retentionDays = retentionDays;
    }

    public String getDateTimeFormat() {
        return dateTimeFormat;
    }

    public void setDateTimeFormat(String dateTimeFormat) {
        this.dateTimeFormat = dateTimeFormat;
    }

    public String getPostfix() {
        return postfix;
    }

    public void setPostfix(String postfix) {
        this.postfix = postfix;
    }

    @Override
    public String toString() {
        return "EagleAccessLogProperties{" +
                "timezone='" + timezone + '\'' +
                ", retentionDays=" + retentionDays +
                ", postfix='" + postfix + '\'' +
                ", logName='" + logName + '\'' +
                ", extendedInfoEnabled=" + extendedInfoEnabled +
                ", enabled=" + enabled +
                ", dateTimeFormat='" + dateTimeFormat + '\'' +
                ", appendEnabled=" + appendEnabled +
                '}';
    }
}
