package onl.tesseract.lib.profile

import com.destroystokyo.paper.profile.PlayerProfile
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.util.*

/**
 * Storage class for cached player profiles
 */
class PlayerProfileService {

    private val profileMap: MutableMap<UUID, PlayerProfile> = mutableMapOf()

    fun getPlayerSkinProfile(playerUUID: UUID): PlayerProfile {
        return profileMap[playerUUID] ?: preloadPlayerProfile(playerUUID)
    }

    fun createProfile(): PlayerProfile {
        return Bukkit.createProfile(UUID.randomUUID())
    }

    /**
     * Get the head of a player. May perform a blocking request to complete the profile, be
     * careful to call this method in an async context. The profile will be cached for next retrievals.
     */
    fun getPlayerHead(uuid: UUID): ItemStack {
        val playerProfile = getPlayerSkinProfile(uuid)
        val item = ItemStack(Material.PLAYER_HEAD)
        item.editMeta { meta ->
            meta as SkullMeta
            meta.playerProfile = playerProfile
        }
        return item
    }

    private fun registerPlayerProfile(uuid: UUID, profile: PlayerProfile) {
        profileMap[uuid] = profile
    }

    /**
     * Loads the player profile to store the skin texture. Will make a blocking request to complete the profile, be
     * careful to call this method in an async context
     */
    fun preloadPlayerProfile(playerUUID: UUID): PlayerProfile {
        // Get the PlayerProfile in order to store the skin texture to avoid lag later.
        val playerProfile = Bukkit.createProfile(playerUUID)
        playerProfile.complete()
        registerPlayerProfile(playerUUID, playerProfile)
        return playerProfile
    }
}
