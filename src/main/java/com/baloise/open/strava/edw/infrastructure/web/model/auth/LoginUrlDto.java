package com.baloise.open.strava.edw.infrastructure.web.model.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@EqualsAndHashCode
public class LoginUrlDto {

    @Schema(name = "url",
            required = true,
            description = "Provides the URL that is used to call the Strava authorization page")
    private String url;
}
