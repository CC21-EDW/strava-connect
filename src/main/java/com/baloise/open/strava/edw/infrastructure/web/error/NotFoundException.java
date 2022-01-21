package com.baloise.open.strava.edw.infrastructure.web.error;

import org.slf4j.helpers.MessageFormatter;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class NotFoundException extends AbstractThrowableProblem {

    public NotFoundException(String key, String message) {
        super(null, "Not found", Status.NOT_FOUND, MessageFormatter.format(message, key).getMessage());
    }
}
