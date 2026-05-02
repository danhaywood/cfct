package com.danhaywood.cfct.implspring;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cfct.comparison.ignore-column-advisors")
public class IgnoreColumnAdvisorsProperties {

    private boolean identityEnabled = true;
    private boolean uuidEnabled = true;
    private boolean timestampsEnabled = true;
    private boolean extendedPropertiesEnabled = true;

    public boolean isIdentityEnabled() {
        return identityEnabled;
    }

    public void setIdentityEnabled(final boolean identityEnabled) {
        this.identityEnabled = identityEnabled;
    }

    public boolean isUuidEnabled() {
        return uuidEnabled;
    }

    public void setUuidEnabled(final boolean uuidEnabled) {
        this.uuidEnabled = uuidEnabled;
    }

    public boolean isTimestampsEnabled() {
        return timestampsEnabled;
    }

    public void setTimestampsEnabled(final boolean timestampsEnabled) {
        this.timestampsEnabled = timestampsEnabled;
    }

    public boolean isExtendedPropertiesEnabled() {
        return extendedPropertiesEnabled;
    }

    public void setExtendedPropertiesEnabled(final boolean extendedPropertiesEnabled) {
        this.extendedPropertiesEnabled = extendedPropertiesEnabled;
    }
}
