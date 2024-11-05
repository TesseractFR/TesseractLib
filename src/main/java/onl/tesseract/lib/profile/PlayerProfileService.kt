package onl.tesseract.lib.profile

import onl.tesseract.tesseractlib.TesseractLib
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

data class PlayerSkinProfile(val skinValue: String, val skinSignature: String)

/**
 * Storage class for cached player profiles
 */
class PlayerProfileService {

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

    private fun registerPlayerProfile(uuid: UUID, profile: PlayerSkinProfile) {
        profileMap[uuid] = profile
    }

    /**
     * Loads the player profile to store the skin texture asynchronously
     */
    fun preloadPlayerProfile(playerUUID: UUID, callback: ((PlayerSkinProfile) -> Unit)? = null) {
        // Get the PlayerProfile in order to store the skin texture to avoid lag later.
        val playerProfile = Bukkit.createProfile(playerUUID)
        object : BukkitRunnable() { // TODO : use TaskManager
            override fun run() {
                playerProfile.complete()
                playerProfile.properties
                    .find { it.name == "textures" }
                    ?.let {
                        val profile = PlayerSkinProfile(it.value, it.signature!!)
                        registerPlayerProfile(playerUUID, profile)
                        callback?.invoke(profile)
                    }
            }
        }.runTaskAsynchronously(TesseractLib.instance)
    }
}