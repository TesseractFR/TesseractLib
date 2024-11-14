package onl.tesseract.lib;

import onl.tesseract.lib.util.Util;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A generic loot table is used to randomly get different loots each time it is queried.
 * The loot table is constructed with the list of all possible loots and their probability.
 */
public class GenericLootTable<T> {
    // Map item to probability
    protected Map<Loot<T>, Float> loots;
    protected LinkedHashMap<Float, Loot<T>> distributionTable;

    public GenericLootTable(final Map<Loot<T>, Float> loots)
    {
        this.loots = loots;
    }

    private LinkedHashMap<Float, Loot<T>> generateDistributionTable()
    {
        if (loots.isEmpty())
            return new LinkedHashMap<>();
        LinkedHashMap<Float, Loot<T>> result = new LinkedHashMap<>();

        final float totalProba = loots.values().stream().reduce(Float::sum).orElse(0f);
        if (totalProba == 0f)
            return new LinkedHashMap<>();
        float cumulatedProba = 0f;
        for (final Map.Entry<Loot<T>, Float> entry : loots.entrySet())
        {
            final float lootEffectiveProba = entry.getValue();
            cumulatedProba += lootEffectiveProba;
            result.put(cumulatedProba, entry.getKey());
        }
        return result;
    }

    /**
     * Generates and cache the distribution table
     */
    public void generate()
    {
        this.distributionTable = generateDistributionTable();
    }

    public T peek()
    {
        if (distributionTable == null)
            generate();
        Float max = loots.values().stream().reduce(Float::sum).orElse(0f);
        final double random = Math.random() * max;

        for (final Map.Entry<Float, Loot<T>> entry : distributionTable.entrySet())
        {
            if (random < entry.getKey())
                return entry.getValue().getLoot();
        }
        throw new AssertionError();
    }

    public Collection<T> peek(final int amount)
    {
        final Collection<T> generatedLoots = new ArrayList<>();
        for (int i = 0; i < amount; i++)
        {
            generatedLoots.add(peek());
        }
        return generatedLoots;
    }

    public Collection<T> peek(final int min, final int max)
    {
        if (max < min)
            throw new IllegalArgumentException("max < min");
        return peek((int) Util.random(min, max));
    }

    @FunctionalInterface
    public interface Loot<T> {

        @NotNull
        T getLoot();
    }
}
