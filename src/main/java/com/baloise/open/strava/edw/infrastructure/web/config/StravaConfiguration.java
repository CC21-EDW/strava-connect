package com.baloise.open.strava.edw.infrastructure.web.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class StravaConfiguration {
    @Value("${strava.auth.token-url}")
    private String tokenUrl;

    @Value("${strava.auth.authorization-url}")
    private String authorizationUrl;

    @Value("${strava.auth.redirect-url}")
    private String redirectUrl;

    @Value("${strava.api.base-path}")
    private String apiBasePath;

    @Value("${strava.api.security}")
    private String apiSecurity;

    @Value("${server.servlet.context-path}")
    private String contextPath;
}
