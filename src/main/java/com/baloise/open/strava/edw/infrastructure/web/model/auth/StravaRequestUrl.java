package com.baloise.open.strava.edw.infrastructure.web.model.auth;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@EqualsAndHashCode
public class StravaRequestUrl {

    private String baseUrl;

    private String clientId;

    private String accessToken;

    private String activitiesUrl;

    public String getActivitiesUrl(){
        return baseUrl + "/activities" +
                "?client_id=" + clientId +
                "&access_token=" + accessToken +
                "&page=1";
    }
}
