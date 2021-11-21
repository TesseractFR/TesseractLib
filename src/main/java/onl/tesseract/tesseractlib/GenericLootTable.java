package onl.tesseract.tesseractlib;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class GenericLootTable<T> {
    // Map item to probability
    protected Map<Loot<T>, Float> loots;
    protected Map<Float, Loot<T>> distributionTable;

    public GenericLootTable(final Map<Loot<T>, Float> loots)
    {
        this.loots = loots;
    }

    private Map<Float, Loot<T>> generateDistributionTable()
    {
        if (loots.isEmpty())
            return Map.of();
        Map<Float, Loot<T>> result = new HashMap<>();

        final float totalProba = loots.values().stream().reduce(Float::sum).orElse(0f);
        final float probaRatio = 1 / totalProba;
        float cumulatedProba = 0f;
        for (final Map.Entry<Loot<T>, Float> entry : loots.entrySet())
        {
            final float lootEffectiveProba = entry.getValue() * probaRatio;
            cumulatedProba += lootEffectiveProba;
            result.put(cumulatedProba, entry.getKey());
        }
        return result;
    }

    public void generate()
    {
        this.distributionTable = generateDistributionTable();
    }

    public T peek()
    {
        if (distributionTable == null)
            throw new IllegalStateException("Must be generated");
        final double random = Math.random();

        for (final Map.Entry<Float, Loot<T>> entry : distributionTable.entrySet())
        {
            if (random < entry.getKey())
                return entry.getValue().getLoot();
        }
        throw new AssertionError();
    }

    @FunctionalInterface
    public interface Loot<T> {

        @NotNull
        T getLoot();
    }
}
