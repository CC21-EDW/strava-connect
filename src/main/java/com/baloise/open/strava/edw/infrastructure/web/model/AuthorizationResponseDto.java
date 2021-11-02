package com.baloise.open.strava.edw.infrastructure.web.model;

import com.baloise.open.strava.edw.infrastructure.web.serialization.LocalDateTimeDeserializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class AuthorizationResponseDto {

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("expires_at")
    @JsonDeserialize(using= LocalDateTimeDeserializer.class)
    private LocalDateTime expiresAt;

    @JsonProperty("expires_in")
    private int expiresIn;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty( "athlete")
    private AthleteDto athlete;
}
