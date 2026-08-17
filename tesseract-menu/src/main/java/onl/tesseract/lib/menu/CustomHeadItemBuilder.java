package onl.tesseract.lib.menu;

import com.destroystokyo.paper.profile.ProfileProperty;
import onl.tesseract.lib.profile.PlayerProfileService;
import onl.tesseract.lib.service.ServiceContainer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;


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

    public ItemStack build(PlayerProfileService profileService){
        ItemStack item = material(Material.PLAYER_HEAD).build();

        item.editMeta(meta -> {
            var profile = profileService.createProfile();
            profile.setProperty(new ProfileProperty("textures", data, signature));
            ((SkullMeta) meta).setPlayerProfile(profile);
        });

        return item;
    }

    public ItemStack build(){
        return build(ServiceContainer.get(PlayerProfileService.class));
    }

}
