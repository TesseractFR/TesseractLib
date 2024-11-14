package onl.tesseract.lib.util;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.StringJoiner;

public abstract class TerritoryMapper {

    @Nullable
    protected abstract String getChunkHolder(final Chunk chunk);

    public void display(CommandSender sender, Location location)
    {
        Direction dir = Direction.of(location.getYaw());

        Chunk[] chunks = getChunks(dir.xStep, dir.zStep, dir, location.getChunk());

        display(sender, chunks);
    }

    private void display(final CommandSender sender, final Chunk[] chunks)
    {
        HashMap<String, ChatColor> claims = new HashMap<>();
        ChatColor[] colors = new ChatColor[] { ChatColor.GREEN, ChatColor.GOLD, ChatColor.AQUA, ChatColor.LIGHT_PURPLE };
        // Display
        for (int i = 0; i < chunks.length; i += 9)
        {
            StringBuilder line = new StringBuilder();
            for (int k = 0; k < 9; k++)
            {
                String holder = getChunkHolder(chunks[i + k]);
//                Location location = chunks[i + k].getBlock(0, 0, 0).getLocation();
//                var cuboids= Cuboid.getCuboids(location);
//                var guild = Guild.fromCuboids(cuboids);

                if (holder == null)
                {
                    if (i + k == 40)
                        line.append(ChatColor.WHITE).append("^");
                    else
                        line.append(ChatColor.GRAY).append("-");
                }
                if (holder != null)
                {
                    if (!claims.containsKey(holder))
                        claims.put(holder, colors[claims.size()]);
                    ChatColor color = claims.get(holder);
                    if (i + k == 40)
                        line.append(color).append("^");
                    else
                        line.append(color).append("+");
                }
            }
            sender.sendMessage(line.toString());
        }

        StringJoiner joiner = new StringJoiner(ChatColor.GRAY + " | ");
        claims.keySet().stream()
              .map(v -> claims.get(v) + v)
              .forEach(joiner::add);
        sender.sendMessage(joiner.toString());
    }

    private Chunk[] getChunks(final int step1, final int step2, final Direction dir, final Chunk center)
    {
        Chunk[] chunks = new Chunk[81];
        int i = 0;

        for (int a = 4 * -step1; step1 == 1 ? a <= 4 : a >= -4; a += step1)
        {
            for (int b = 4 * -step2; step2 == 1 ? b <= 4 : b >= -4; b += step2)
            {
                if (dir.zFirst)
                    chunks[i++] = center.getWorld().getChunkAt(center.getX() + a, center.getZ() + b);
                else
                    chunks[i++] = center.getWorld().getChunkAt(center.getX() + b, center.getZ() + a);
            }
        }
        return chunks;
    }

    private enum Direction
    {
        NORTH(false, 1, 1),
        EAST(true, 1, -1),
        SOUTH(false, -1, -1),
        WEST(true, -1, 1),
        ;

        public final boolean zFirst;
        public final int zStep;
        public final int xStep;

        Direction(boolean zFirst, int zStep, int xStep)
        {
            this.zFirst = zFirst;
            this.zStep = zStep;
            this.xStep = xStep;
        }

        public static Direction of(double yaw)
        {
            if (yaw < 0)
                yaw = 360 + yaw;
            if (yaw <= 45 || yaw >= 315)
                return SOUTH;
            if (yaw >= 45 && yaw <= 135)
                return WEST;
            if (yaw >= 135 && yaw <= 225)
                return NORTH;
            return EAST;
        }
    }
}
