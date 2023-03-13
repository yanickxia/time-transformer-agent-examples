package com.topdesk.timetransformer;

import com.topdesk.timetransformer.agent.DoNotInstrument;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;

/**
 * Default implementation of Time which redirects all calls to {@link System#currentTimeMillis} and {@link System#nanoTime}.
 *
 * <p>Calls from this class to {@code System.currentTimeMillis} and {@code System.nanoTime} will not be redirected by the agent.
 */
@DoNotInstrument
public enum DefaultTime implements Time {
    INSTANCE;

    @Override
    public long currentTimeMillis() {
        return fakeZoneDateTime().toInstant().toEpochMilli();
    }

    @Override
    public long nanoTime() {
        final Instant instant = fakeZoneDateTime().toInstant();
        return (TimeUnit.SECONDS.toNanos(instant.getEpochSecond()) + instant.getNano());
    }

    @Override
    public String toString() {
        return super.toString() + ", DefaultTime";
    }

    private ZonedDateTime fakeZoneDateTime() {
        String fakeFixedTime = System.getenv("FAKE_FIXED_TIME");
        String fakeOffsetTime = System.getenv("FAKE_OFFSET_TIME");

        if (fakeFixedTime != null) {
            return ZonedDateTime.parse(fakeFixedTime);
        }
        ZonedDateTime now = ZonedDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis()), ZoneId.systemDefault());
        if (fakeOffsetTime != null) {
            char operator = fakeOffsetTime.charAt(0);
            Duration offset = Duration.parse(fakeOffsetTime.substring(1));
            switch (operator) {
                case '+':
                    now = now.plus(offset);
                    break;
                case '-':
                    now = now.minus(offset);
                    break;
            }

            return now;
        }

        return now;
    }
}
