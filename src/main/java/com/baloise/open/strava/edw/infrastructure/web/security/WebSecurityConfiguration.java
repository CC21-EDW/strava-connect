package com.baloise.open.strava.edw.infrastructure.web.security;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
public class WebSecurityConfiguration {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // no super
        http
                .cors().and()
                .csrf().disable()
                .headers().frameOptions().disable();

        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.authorizeRequests()
                //.mvcMatchers("/coverages/*").hasRole("AuthenticatedRole")
                //.antMatchers(getWhitelistedPaths()).permitAll()
                //.anyRequest().authenticated()
                .anyRequest().permitAll();
        //.and()
        //.oauth2ResourceServer()
        //.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()));
        return http.build();
    }

    private String[] getWhitelistedPaths() {
        return new String[]{
                "/index.html",
                "/api-docs/**",
                "/api-docs.yml",
                "/api-docs.yaml",
                "/swagger-ui.html",
                "/swagger-ui/**",
                "/openapi*",
                "/actuator/**"
        };
    }

//    private Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter() {
//        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
//        jwtConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRealmRoleConverter());
//        jwtConverter.setPrincipalClaimName("preferred_username");
//        return jwtConverter;
//    }
}
