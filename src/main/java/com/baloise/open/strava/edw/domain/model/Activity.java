package com.baloise.open.strava.edw.domain.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class Activity {

    private String name;

    private BigDecimal distance;

    private int movingTime;

    private int elapsedTime;

    private LocalDateTime startDate;

    private String timezone;

    private String type;
}
