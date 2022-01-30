package com.baloise.open.strava.edw.infrastructure.web.openapi;

import com.baloise.open.strava.edw.domain.model.Activity;
import com.baloise.open.strava.edw.infrastructure.web.model.auth.LoginUrlDto;
import com.baloise.open.strava.edw.infrastructure.web.model.auth.StravaRequestUrl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Tag(name = "strava-connect", description = "Provides access to Strava activities and further information.")
@Validated
public interface StravaConnectOpenApi {
    @Operation(summary = "Creates a login URL to be used to start login.",
            method = "GET",
            operationId = "login-url",
            parameters = {@Parameter(name = "client_id"),
                    @Parameter(name = "client_secret")})
    LoginUrlDto loginUrl(String clientId,
                         String clientSecret);

    @Operation(summary = "CALLED BY STRAVA! Provides all URls for authenticated access of entities.",
            hidden = true,
            method = "GET",
            operationId = "authenticate",
            parameters = {@Parameter(name = "client_id"),
                    @Parameter(name = "code"),
                    @Parameter(name = "state"),
                    @Parameter(name = "scope")})
    StravaRequestUrl authenticate(@RequestParam(name = "client_id") String clientId,
                                        @RequestParam String code,
                                        @RequestParam String state,
                                        @RequestParam(required = false) String scope,
                                        HttpServletRequest httpRequest);

    @Operation(summary = "Provides all activities as paged requests.",
            method = "GET",
            operationId = "activities",
            parameters = {@Parameter(name = "client_id"),
                    @Parameter(name = "access_token"),
                    @Parameter(name = "page")})
    List<Activity> getActivities(@RequestParam(name = "client_id") String clientId,
                                 @RequestParam(name = "access_token") String accessToken,
                                 @RequestParam(required = false) Integer page);
}
