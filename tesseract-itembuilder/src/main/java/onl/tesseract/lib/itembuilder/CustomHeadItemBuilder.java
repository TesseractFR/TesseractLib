package onl.tesseract.lib.itembuilder;

import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;


public class CustomHeadItemBuilder extends AItemBuilder<CustomHeadItemBuilder>{

    private final String data;
    private final String signature;

    public CustomHeadItemBuilder(String data, String signature, AItemBuilder<?> builder) {
        super(Material.PLAYER_HEAD, null, builder);
        this.data = data;
        this.signature = signature;
    }

    @Override
    public CustomHeadItemBuilder self(){
        return this;
    }

    public ItemStack build(){
        ItemStack item = material(Material.PLAYER_HEAD).build();

        item.editMeta(meta -> {
            var profile = Bukkit.createProfile(UUID.randomUUID());
            profile.setProperty(new ProfileProperty("textures", data, signature));
            ((SkullMeta) meta).setPlayerProfile(profile);
        });

        return item;
    }
}
