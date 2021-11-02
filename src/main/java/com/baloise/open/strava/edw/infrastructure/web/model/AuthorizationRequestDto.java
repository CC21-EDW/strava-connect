package com.baloise.open.strava.edw.infrastructure.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AuthorizationRequestDto {
    @JsonProperty("client_id")
    String clientId;

    @JsonProperty("client_secret")
    String clientSecret;

    String code;

    @JsonProperty("grant_type")
    String grantType;
}
