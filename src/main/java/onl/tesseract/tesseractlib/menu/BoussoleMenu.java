package onl.tesseract.tesseractlib.menu;

import onl.tesseract.tesseractlib.player.TPlayer;
import onl.tesseract.tesseractlib.util.menu.InventoryMenu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BoussoleMenu extends InventoryMenu {
    static final ItemStack linkHead = getCustomHead(ChatColor.GOLD + "Lien du site internet", "ewogICJ0aW1lc3RhbXAiIDogMTU5Mjc0MjM2MzYyNSwKICAicHJvZmlsZUlkIiA6ICJmNjE1NzFmMjY1NzY0YWI5YmUxODcyMjZjMTEyYWEwYSIsCiAgInByb2ZpbGVOYW1lIiA6ICJGZWxpeF9NYW5nZW5zZW4iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmVjYWY2NTNhMDlhYzRjNDVlY2Q2MmJlZDM0OWFhN2E3ZDlmYjVmZjlmNTc1MmUyZTc0MzJlMzNmYWYyNmUxMCIKICAgIH0KICB9Cn0=", "ZBKHtPt7TPnWBRjepXZ6xM+68bQGwF5Bpe1X8B077cKy60/wGNvrXgp5kM4DW3frg6QL6xFkBwkwAV1YsKgtxM2W9zRCJP2WSyySIi6f5DrtIKCevpNvlSZW7uI51ZLKZQtpLhNTgME9hK+uArqTo9kcL6FF8sukXSMKdUBF5FleTQfKRhDr0CWCUM324T5OdKM0wzX/+4T5FRpF/65ptp48bQ/SeI9EesjNG6KV2LJmmg5v5I0zkjgstv7zX67cUPjntV2MAfziC+Vv3C3XTSUoFKUQUVDt5Ydfc5Kr2fmJTY9hj16ReV654Ou47qz06zpymVSaLfnYlfCDw2rQ/CTjp251+A77ptDNIYsH5yUOzJBEdEiJX1b5rI5SChx3+FwTDeBeSp5kq5QBrvW/rITXBBdcI12w/9y8gMv3B6ozwjH3erdnorwnTWfGO8zxA7AXj/kXtunK0CIVQ+iwMZtxaocq2C3AC2AxeTflK8Duz9DnABLt01AR2flXj/M6BsBB1DVyJrn482Uc8K1+30S7reXM53Ze4EfoAb5IKloS1KEGH3NjRMhhQ9rAxVukCE2znxaYHhStQZuO/Ztr8jz3EdXNuHYc9oo2s41foMfBNCnzS/W+0TPM9KQ+UrRrJiwwvJ+zXwHWAJFB5cHARexOctWpDC3zTObnISHf9T0=");
    static final ItemStack twitterHead = getCustomHead(ChatColor.GOLD + "Lien du twitter", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2M3NDVhMDZmNTM3YWVhODA1MDU1NTkxNDllYTE2YmQ0YTg0ZDQ0OTFmMTIyMjY4MThjMzg4MWMwOGU4NjBmYyJ9fX0=", "cc745a06f537aea80505559149ea16bd4a84d4491f12226818c3881c08e860fc");
    static final ItemStack queteHead = getCustomHead(ChatColor.GOLD + "Les quêtes", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2RjOTg1YTdhNjhjNTc0ZjY4M2MwYjg1OTUyMWZlYjNmYzNkMmZmYTA1ZmEwOWRiMGJhZTQ0YjhhYzI5YjM4NSJ9fX0=", "7dc985a7a68c574f683c0b859521feb3fc3d2ffa05fa09db0bae44b8ac29b385");
    static final ItemStack discordHead = getCustomHead(ChatColor.GOLD + "Lien du discord", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzg3M2MxMmJmZmI1MjUxYTBiODhkNWFlNzVjNzI0N2NiMzlhNzVmZjFhODFjYmU0YzhhMzliMzExZGRlZGEifX19", "7873c12bffb5251a0b88d5ae75c7247cb39a75ff1a81cbe4c8a39b311ddeda");
    static final ItemStack facebookHead = getCustomHead(ChatColor.GOLD + "Lien du facebook", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGViNDYxMjY5MDQ0NjNmMDdlY2ZjOTcyYWFhMzczNzNhMjIzNTliNWJhMjcxODIxYjY4OWNkNTM2N2Y3NTc2MiJ9fX0=", "deb46126904463f07ecfc972aaa37373a22359b5ba271821b689cd5367f75762");
    static final ItemStack boutiqueHead = getCustomHead(ChatColor.GOLD + "Lien de la boutique", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzhmODhiMTYxNzYzZjYyZTRjNTFmNWViMWQzOGZhZjNiODJjNDhhODM5YWMzMTcxMjI5NTU3YWRlNDI3NDM0In19fQ==", "78f88b161763f62e4c51f5eb1d38faf3b82c48a839ac3171229557ade427434");
    TPlayer player;

    public BoussoleMenu(TPlayer player)
    {
        super(54, ChatColor.BLUE + "Boussole des voeux");
        this.player = player;
    }

    @Override
    public void open(Player viewer) {

    }
}
