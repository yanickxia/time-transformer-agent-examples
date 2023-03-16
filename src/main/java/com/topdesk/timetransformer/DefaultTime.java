package com.topdesk.timetransformer;

import com.topdesk.timetransformer.agent.DoNotInstrument;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Default implementation of Time which redirects all calls to {@link System#currentTimeMillis} and {@link System#nanoTime}.
 *
 * <p>Calls from this class to {@code System.currentTimeMillis} and {@code System.nanoTime} will not be redirected by the agent.
 */
@DoNotInstrument
public enum DefaultTime implements Time {
    INSTANCE;

    private static Duration OFFSET = Duration.ZERO;
    private static final String LD_ENV_NAME = "AGENT_LD_PRELOAD";
    private static final String FAKE_TIME_LD_ENV = "LD_PRELOAD";

    static {
        initOffset();
    }

    @Override
    public long currentTimeMillis() {
        return System.currentTimeMillis() + OFFSET.toMillis();
    }

    @Override
    public long nanoTime() {
        return System.nanoTime() + OFFSET.toNanos();
    }

    @Override
    public String toString() {
        return super.toString() + ", DefaultTime";
    }

    private static void initOffset() {
        if (System.getenv(LD_ENV_NAME) == null) {
            return;
        }
        ZonedDateTime realNow = ZonedDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis()), ZoneId.systemDefault());
        ZonedDateTime offsetNow = fakeZoneDateTime();
        OFFSET = Duration.between(realNow, offsetNow);
        System.out.println("offset time: " + OFFSET.toString());
    }

    private static ZonedDateTime fakeZoneDateTime() {
        try {
            Runtime r = Runtime.getRuntime();
            final Map<String, String> envs = new HashMap<>(System.getenv());
            envs.put(FAKE_TIME_LD_ENV, envs.get(LD_ENV_NAME));
            Process p = r.exec("date -u +\"%Y-%m-%dT%H:%M:%SZ\"", toStrings(envs));
            p.waitFor();
            BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = b.readLine();
            b.close();
            return ZonedDateTime.parse(line.replaceAll("\"", ""));
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        return null;
    }

    private static String[] toStrings(final Map<String, String> environment) {
        if (environment == null) {
            return null;
        }
        final String[] result = new String[environment.size()];
        int i = 0;
        for (final Map.Entry<String, String> entry : environment.entrySet()) {
            final String key = entry.getKey() == null ? "" : entry.getKey();
            final String value = entry.getValue() == null ? "" : entry.getValue();
            result[i] = key + "=" + value;
            i++;
        }
        return result;
    }
}
