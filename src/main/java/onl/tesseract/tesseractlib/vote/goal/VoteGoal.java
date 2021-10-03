package onl.tesseract.tesseractlib.vote.goal;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public class VoteGoal {
    private final int id;
    private final Instant start;
    private final Instant end;
    private final int requiredQuantity;

    public VoteGoal(final int id, final Instant start, final Instant end, final int requiredQuantity)
    {
        this.id = id;
        this.start = start;
        this.end = end;
        this.requiredQuantity = requiredQuantity;
    }

    public int getId()
    {
        return id;
    }

    public Instant getStart()
    {
        return start;
    }

    public Instant getEnd()
    {
        return end;
    }

    public int getRequiredQuantity()
    {
        return requiredQuantity;
    }

    public Duration getDuration()
    {
        return Duration.between(start, end);
    }

    public Duration getRemainingDuration()
    {
        return Duration.between(Instant.now(), end);
    }

    public String getPrintableDuration()
    {
        Duration duration = getDuration();
        long days = duration.toDays();
        if (days > 0)
            return String.format("%dj", days);
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        if (minutes % 60 < 10)
        {
            return String.format("%dh", hours + (minutes < 10 ? 0 : 1));
        }
        return String.format("%dh%02dm", hours, minutes);
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        final VoteGoal voteGoal = (VoteGoal) o;
        return id == voteGoal.id;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id);
    }
}
