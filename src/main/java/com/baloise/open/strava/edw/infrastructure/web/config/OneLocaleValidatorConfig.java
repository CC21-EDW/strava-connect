package com.baloise.open.strava.edw.infrastructure.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;

import java.util.Locale;

/**
 * Ensures that we only use English as locale for messages in bean validation.
 */
@Configuration
public class OneLocaleValidatorConfig {
    @Bean
    LocaleResolver localeResolver() {
        // Force english for Spring Security error messages
        return new FixedLocaleResolver(Locale.ENGLISH);
    }
}
