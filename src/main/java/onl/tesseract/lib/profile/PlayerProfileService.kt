package onl.tesseract.lib.profile

import com.destroystokyo.paper.profile.PlayerProfile
import onl.tesseract.lib.task.TaskScheduler
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.util.*

data class PlayerSkinProfile(val skinValue: String, val skinSignature: String)

/**
 * Storage class for cached player profiles
 */
class PlayerProfileService(private val scheduler: TaskScheduler) {

    private val profileMap: MutableMap<UUID, PlayerSkinProfile> = mutableMapOf()

    fun getPlayerSkinProfile(playerUUID: UUID): PlayerSkinProfile? = profileMap[playerUUID]

    fun getPlayerSkinProfile(playerUUID: UUID, callback: (PlayerSkinProfile) -> Unit) {
        profileMap[playerUUID].let {
            if (it != null) {
                callback(it)
            } else {
                preloadPlayerProfile(playerUUID, callback)
            }
        }
    }

    fun createProfile(): PlayerProfile {
        return Bukkit.createProfile(UUID.randomUUID())
    }

    fun getPlayerHead(uuid: UUID): ItemStack {
        val playerProfile = Bukkit.createProfile(uuid)
        playerProfile.complete()
        val item = ItemStack(Material.PLAYER_HEAD)
        item.editMeta { meta ->
            meta as SkullMeta
            meta.playerProfile = playerProfile
        }
        return item
    }

    private fun registerPlayerProfile(uuid: UUID, profile: PlayerSkinProfile) {
        profileMap[uuid] = profile
    }

    /**
     * Loads the player profile to store the skin texture asynchronously
     */
    fun preloadPlayerProfile(playerUUID: UUID, callback: ((PlayerSkinProfile) -> Unit)? = null) {
        // Get the PlayerProfile in order to store the skin texture to avoid lag later.
        val playerProfile = Bukkit.createProfile(playerUUID)
        scheduler.runAsync {
            playerProfile.complete()
            playerProfile.properties
                .find { it.name == "textures" }
                ?.let {
                    val profile = PlayerSkinProfile(it.value, it.signature!!)
                    registerPlayerProfile(playerUUID, profile)
                    callback?.invoke(profile)
                }
        }
    }
}