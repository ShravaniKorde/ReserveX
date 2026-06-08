package com.reservex.common.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Shared date / time helpers.
 * All public-facing timestamps are ISO-8601 UTC strings (as per API spec).
 */
public final class DateUtil {

    private DateUtil() {}

    private static final ZoneId IST = ZoneId.of("Asia/Kolkata");
    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    /** Current instant as UTC Instant. */
    public static Instant nowUtc() {
        return Instant.now();
    }

    /** Format any Instant as an ISO-8601 UTC string e.g. 2026-06-01T10:00:00Z */
    public static String formatUtc(Instant instant) {
        if (instant == null) return null;
        return instant.atZone(ZoneId.of("UTC")).format(ISO);
    }

    /** Format any Instant in IST (for logging / display only — API responses use UTC). */
    public static String formatIst(Instant instant) {
        if (instant == null) return null;
        return instant.atZone(IST).format(ISO);
    }

    /** Return an Instant that is {@code seconds} from now. */
    public static Instant plusSeconds(long seconds) {
        return Instant.now().plusSeconds(seconds);
    }
}