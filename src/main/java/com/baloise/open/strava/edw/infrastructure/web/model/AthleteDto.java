package com.baloise.open.strava.edw.infrastructure.web.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Missing: "athlete":{"bio":null, "friend":null,"follower":null}
 */
@Data
@NoArgsConstructor
public class AthleteDto {
    @JsonProperty("id")
    private String id;

    @JsonProperty("username")
    private String userName;

    @JsonProperty("resource_state")
    private int resourceState;

    @JsonProperty("firstname")
    private String firstname;

    @JsonProperty("lastname")
    private String lastname;

    @JsonProperty("city")
    private String city;

    @JsonProperty("state")
    private String state;

    @JsonProperty("country")
    private String country;

    @JsonProperty("sex")
    private String sex;

    @JsonProperty("premium")
    private boolean premium;

    @JsonProperty("summit")
    private boolean summit;

    @JsonProperty("created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone="UTC")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone="UTC")
    private LocalDateTime updatedAt;

    @JsonProperty("badge_type_id")
    private int badgeTypeId;

    @JsonProperty("weight")
    private int weight;

    @JsonProperty("profile_medium")
    private String profileMedium;

    @JsonProperty("profile")
    private String profile;
}
