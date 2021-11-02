package com.baloise.open.strava.edw.infrastructure.web.mapper;

import com.baloise.open.strava.client.model.SummaryActivityDto;
import com.baloise.open.strava.edw.domain.model.Activity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@Mapper
public interface ActivityDtoMapper {
    ActivityDtoMapper INSTANCE = Mappers.getMapper(ActivityDtoMapper.class);

    Activity map(SummaryActivityDto activity);

    List<Activity> map(List<SummaryActivityDto> activities);

    default LocalDateTime map(OffsetDateTime dateTime) {
        return dateTime.toLocalDateTime();
    }
}
