package com.baloise.open.strava.edw.infrastructure.kafka.mapper;

import com.baloise.open.edw.infrastructure.kafka.model.ActivityDto;
import com.baloise.open.strava.client.model.SummaryActivityDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.time.OffsetDateTime;
import java.util.List;

@Mapper
public interface ActivityDtoMapper {
    ActivityDtoMapper INSTANCE = Mappers.getMapper(ActivityDtoMapper.class);

    ActivityDto map(SummaryActivityDto activity);

    List<ActivityDto> map(List<SummaryActivityDto> activities);

    default Long map(OffsetDateTime dateTime) {
        return dateTime.toEpochSecond();
    }
}
