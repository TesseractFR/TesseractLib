package onl.tesseract.tesseractlib.util;

import java.util.concurrent.TimeUnit;

public class Ticks {

    public static final long TICKS_PER_SECONDS = 20;
    public static final long TICKS_PER_MINUTES = 60 * 20;
    public static final long TICKS_PER_HOURS = 60 * 60 * 20;
    public static final long TICKS_PER_DAY = 24 * 60 * 60 * 20;

    public static long from(int duration, TimeUnit unit)
    {
        return switch (unit)
                {
                    case NANOSECONDS, MICROSECONDS -> 0;
                    case MILLISECONDS -> duration % 50;
                    case SECONDS -> duration * TICKS_PER_SECONDS;
                    case MINUTES -> duration * TICKS_PER_MINUTES;
                    case HOURS -> duration * TICKS_PER_HOURS;
                    case DAYS -> TICKS_PER_DAY;
                };
    }

    public static long fromMinutes(int minutes)
    {
        return TICKS_PER_MINUTES * minutes;
    }
}
