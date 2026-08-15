package onl.tesseract.lib.event.equipment.invocable

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import onl.tesseract.lib.animation.AnimationTarget
import onl.tesseract.lib.animation.Circle
import onl.tesseract.lib.animation.Concentration
import onl.tesseract.lib.equipment.EquipmentService
import onl.tesseract.lib.equipment.Invocable
import onl.tesseract.lib.service.PluginService
import onl.tesseract.lib.service.ServiceContainer
import onl.tesseract.lib.task.TaskScheduler
import onl.tesseract.lib.menu.ItemBuilder
import onl.tesseract.lib.chat.ChatFormats.ELYTRA_ERROR
import onl.tesseract.lib.chat.ChatFormats.ELYTRA_SUCCESS
import onl.tesseract.lib.util.Util
import onl.tesseract.lib.util.plus
import org.bukkit.*
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.block.BlockFace
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityToggleGlideEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.event.player.PlayerToggleSneakEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitTask
import org.bukkit.util.Vector
import java.util.*

private const val MS_TO_SECONDS = 1000
private const val SPEED_MULTIPLIER = 0.10
private const val PROTECTION_MULTIPLIER = 0.5
private const val PERCENT_CONVERSION = 100

private const val BOOST_BASE = 5
private const val BOOST_LEVEL_MULTIPLIER = 5
private const val MAX_DISPLAYED_BOOSTS = 10
private const val MAX_BOOST_LEVEL = 10

private const val MAX_RECOVERY_TIME = 50000L
private const val RECOVERY_DECREASE_PER_LEVEL = 5000L

private const val BOOST_CONSUMPTION_MULTIPLIER = 0.7
private const val LOW_PITCH = 0.5f
private const val SPEED_THRESHOLD_BASE = 1.20
private const val SPEED_THRESHOLD_STEP = 0.10

private const val ACTIONBAR_INTERVAL_TICKS = 2L
private const val ACTIONBAR_RECHARGE_TICK_MS = 50.0
private const val RECHARGE_FULL_THRESHOLD = 1.0
private const val SPEED_DISPLAY_MULTIPLIER = 20

private const val PARTICLE_DELAY = 0.1f
private const val PARTICLE_RADIUS = 1f
private const val PARTICLE_ROTATION = 3f
private const val LEVITATION_DURATION = 40
private const val SOUND_VOLUME = 20f
private const val ALTITUDE_SOUND_DELAY = 40L

private const val PROPULSION_RADIUS = 2
private const val PROPULSION_COUNT = 20
private const val FIRST_TIMER_DELAY = 30L
private const val SECOND_TIMER_DELAY = 20L
private const val FIREWORK_SOUND_VOLUME = 150f

class Elytra(
    playerUUID: UUID,
    invoked: Boolean,
    handSlot: Int,
    var autoGlide: Boolean = true,
    var protectionLevel: Int = 0,
    var speedLevel: Int = 0,
    var boostChargeLevel: Int = 0,
    var recoveryLevel: Int = 0,
    var currentCharges: Int = 0,
    var rechargeProgress: Double = 0.0
) : Invocable(playerUUID, invoked, handSlot), Listener {
    private var accelerateTask: BukkitTask? = null
    private var actionBarTask: BukkitTask? = null
    private var autoGlideTask: BukkitTask? = null
    private var ignoreSpeedLevel: Boolean = false

    override val excludeOthers: Boolean = true
    override val slotType: EquipmentSlot = EquipmentSlot.CHEST
    override val uniqueName: String = this::class.simpleName!!

    override fun createItem(): ItemStack {
        val boostCount = getBoostCount(boostChargeLevel)
        val recoveryTimeSeconds = getBaseRecoveryTime(recoveryLevel) / MS_TO_SECONDS
        val speedBonus = (SPEED_MULTIPLIER * (speedLevel + 1) * PERCENT_CONVERSION).toInt()
        val protectionBonus = PROTECTION_MULTIPLIER * protectionLevel
        return ItemBuilder(Material.ELYTRA)
            .name(Component.text("Flanc éthéré", NamedTextColor.GOLD))
            .lore()
            .append(Component.text("« Des ailes divines imprégnées de clairvoyance. »", NamedTextColor.DARK_PURPLE))
            .newline()
            .newline()
            .append(Component.text("Vitesse : ", NamedTextColor.DARK_AQUA))
            .append(Component.text("+$speedBonus%", NamedTextColor.GOLD))
            .newline()
            .append(Component.text("Protection : ", NamedTextColor.DARK_AQUA))
            .append(Component.text("$protectionBonus points", NamedTextColor.GOLD))
            .newline()
            .append(Component.text("Boosts max : ", NamedTextColor.DARK_AQUA))
            .append(Component.text(boostCount.toString(), NamedTextColor.GOLD))
            .newline()
            .append(Component.text("Temps recharge : ", NamedTextColor.DARK_AQUA))
            .append(Component.text("1 boost / ${recoveryTimeSeconds}s", NamedTextColor.GOLD))
            .buildLore()
            .enchanted(true)
            .build().apply {
                val meta = this.itemMeta
                val key = NamespacedKey(NamespacedKey.MINECRAFT, "generic.armor")
                meta.addAttributeModifier(
                    Attribute.GENERIC_ARMOR,
                    AttributeModifier(
                        key,
                        protectionLevel.toDouble(),
                        AttributeModifier.Operation.ADD_NUMBER,
                        EquipmentSlotGroup.CHEST
                    )
                )
                meta.isUnbreakable = true
                meta.addItemFlags(
                    org.bukkit.inventory.ItemFlag.HIDE_UNBREAKABLE
                )
                this.itemMeta = meta
            }
    }

    override fun onUninvoke(player: Player, manuelRemoval: Boolean) {
        if (manuelRemoval) {
            isInvoked = false
        }
        ServiceContainer[PluginService::class.java].unregisterEventListener(this)
        actionBarTask?.cancel()
        actionBarTask = null
        autoGlideTask?.cancel()
        autoGlideTask = null
        accelerateTask?.cancel()
        accelerateTask = null
        if (manuelRemoval) animate(player)
    }

    override fun onInvoke(player: Player, manuelInvocation: Boolean) {
        ServiceContainer[PluginService::class.java].registerEventListener(this)
        if (!player.isGliding) {
            player.isGliding = true
        }
        if (currentCharges == 0) currentCharges = getBoostCount(boostChargeLevel)
        displayActionBar(player)
        if (autoGlide) {
            toggleAutoGlideEnabled(true)
        }
        if (manuelInvocation) animate(player)
    }

    @EventHandler
    fun onAccelerate(event: PlayerToggleSneakEvent) {
        val player = event.player
        val canAccelerate = event.isSneaking && isInvoked && player.isGliding && accelerateTask == null

        if (!canAccelerate) return
        if (currentCharges <= 0) {
            player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BASS, 1f, LOW_PITCH)
            return
        }

        accelerateTask = ServiceContainer[TaskScheduler::class.java]
            .runTimer(delay = 0, period = 10L, duration = 0) { task ->
                isManuallyAccelerating = true
                val speed = if (player.location.world.name == "Event") 0 else effectiveSpeedLevel

                if (!player.isOnline || !player.isSneaking || !player.isGliding) {
                    task.cancel()
                    accelerateTask = null
                    isManuallyAccelerating = false
                    return@runTimer
                }

                if (player.velocity.length() < getMaxSpeed(speed)) {
                    currentCharges--
                    player.velocity = player.velocity
                        .add(player.location.direction.multiply(BOOST_CONSUMPTION_MULTIPLIER))
                    player.world.playSound(
                        player.location,
                        Sound.ENTITY_ENDER_DRAGON_FLAP,
                        SoundCategory.PLAYERS, 1f, 1f
                    )
                }
            }
        saveYaml()
    }

    private fun getMaxSpeed(speedLevel: Int): Double {
        return SPEED_THRESHOLD_BASE + SPEED_THRESHOLD_STEP * (speedLevel + 1)
    }

    @EventHandler
    fun onFly(event: EntityToggleGlideEvent) {
        if (event.entityType != EntityType.PLAYER) return
        if (event.entity.uniqueId == playerUUID && isInvoked) {
            val player = event.entity as Player
            if (event.isGliding) displayActionBar(player)
        }
    }

    fun toggleAutoGlideEnabled(autoGlide: Boolean) {
        this.autoGlide = autoGlide
        autoGlideTask?.cancel()
        autoGlideTask = null
        if (!autoGlide) return

        val player = Bukkit.getPlayer(playerUUID) ?: return
        val taskScheduler = ServiceContainer[TaskScheduler::class.java]

        autoGlideTask = taskScheduler.runTimer(delay = 0, period = 10L, duration = 0) { _ ->
            if (!player.isOnline || !isInvoked) return@runTimer

            if (player.hasPotionEffect(PotionEffectType.LEVITATION)) return@runTimer
            val airBelow = (1..4).all {
                player.location.block.getRelative(BlockFace.DOWN, it).isPassable
            }

            if (airBelow && !player.isGliding) {
                player.isGliding = true
                displayActionBar(player)
            }
        }
    }

    @EventHandler
    fun onRespawn(event: PlayerRespawnEvent) {
        if (event.player.uniqueId != playerUUID) return
        if (isInvoked) {
            Bukkit.getScheduler().runTaskLater(
                JavaPlugin.getProvidingPlugin(Elytra::class.java),
                Runnable {
                    val player = event.player
                    if (player.inventory.chestplate == null) {
                        player.inventory.chestplate = getItem()
                    }
                },
                1L
            )
        }
    }

    private fun displayActionBar(player: Player) {
        if (actionBarTask?.isCancelled == false) actionBarTask?.cancel()
        actionBarTask = ServiceContainer[TaskScheduler::class.java].runTimer(0, 2, 0) { task ->
            if (!player.isOnline) {
                task.cancel()
                accelerateTask = null
                return@runTimer
            }
            val rechargeTime = getRecoveryTime(recoveryLevel, player)
            val isFastRecharge = rechargeTime < getBaseRecoveryTime(recoveryLevel)

            if (currentCharges < getBoostCount(boostChargeLevel)) {
                rechargeProgress += (ACTIONBAR_INTERVAL_TICKS * ACTIONBAR_RECHARGE_TICK_MS / rechargeTime)
                if (rechargeProgress >= RECHARGE_FULL_THRESHOLD) {
                    currentCharges++
                    rechargeProgress = 0.0
                }
                saveYaml()
            }
            val comp = Component.text()
            if (player.isGliding) {
                comp.append(Component.text("Vitesse: "))
                    .append(
                        Component.text(
                            (player.velocity.length() * SPEED_DISPLAY_MULTIPLIER).toInt(),
                            NamedTextColor.AQUA
                        )
                    )
                    .append(Component.text(" | ", NamedTextColor.DARK_GRAY))
                    .append(Component.text("Alt: "))
                    .append(Component.text(player.location.blockY, NamedTextColor.GREEN))
                    .append(Component.text(" | ", NamedTextColor.DARK_GRAY))
                    .append(Component.text("Distance: "))
                    .append(
                        Component.text(
                            player.location.distance(player.compassTarget).toInt(),
                            NamedTextColor.YELLOW
                        )
                    )
                    .append(Component.text(" | ", NamedTextColor.DARK_GRAY))
            }
            comp.append(Component.text("Boosts : "))
            comp.append(boostBar())
            if (currentCharges < getBoostCount(boostChargeLevel) && isFastRecharge) {
                comp.append(Component.text(" ⏻", NamedTextColor.LIGHT_PURPLE, TextDecoration.BOLD))
            }

            player.sendActionBar(comp)
        }
    }

    private fun boostBar(): Component {
        val builder = Component.text()
        val maxCharges = getBoostCount(boostChargeLevel)
        val filled = currentCharges
        val recharging = (currentCharges < maxCharges)

        val visibleFilled = minOf(MAX_DISPLAYED_BOOSTS, filled)
        val overflow = filled - visibleFilled
        if (overflow > 0) {
            builder.append(Component.text("(+$overflow) ", NamedTextColor.GREEN, TextDecoration.BOLD))
        }

        repeat(visibleFilled) {
            builder.append(Component.text("⚡", NamedTextColor.GREEN, TextDecoration.BOLD))
        }
        if (recharging) {
            val color = Util
                .getGreenRedGradient((rechargeProgress.coerceIn(0.0, 1.0) * PERCENT_CONVERSION).toInt(), 100)
            builder.append(Component.text("⚡", color, TextDecoration.BOLD))
        }
        val shownCount = visibleFilled + if (recharging) 1 else 0
        val remaining = minOf(maxCharges, MAX_DISPLAYED_BOOSTS) - shownCount
        repeat(remaining) {
            builder.append(Component.text("⚡", TextColor.color(255, 0, 40), TextDecoration.BOLD))
        }
        return builder.build()
    }

    fun getLevel(upgrade: ElytraUpgrade): Int {
        return when (upgrade) {
            ElytraUpgrade.PROTECTION -> protectionLevel
            ElytraUpgrade.SPEED -> speedLevel
            ElytraUpgrade.BOOST_NUMBER -> boostChargeLevel
            ElytraUpgrade.RECOVERY -> recoveryLevel
        }
    }

    fun setLevel(upgrade: ElytraUpgrade, level: Int) {
        when (upgrade) {
            ElytraUpgrade.PROTECTION -> protectionLevel = level
            ElytraUpgrade.SPEED -> speedLevel = level
            ElytraUpgrade.BOOST_NUMBER -> {
                boostChargeLevel = level
                val maxCharges = getBoostCount(level)
                if (currentCharges > maxCharges) {
                    currentCharges = maxCharges
                }
            }

            ElytraUpgrade.RECOVERY -> recoveryLevel = level
        }
        refreshItemInInventory()
    }

    fun upgradeLevel(upgrade: ElytraUpgrade) {
        when (upgrade) {
            ElytraUpgrade.PROTECTION -> protectionLevel++
            ElytraUpgrade.SPEED -> speedLevel++
            ElytraUpgrade.BOOST_NUMBER -> boostChargeLevel++
            ElytraUpgrade.RECOVERY -> recoveryLevel++
        }
        refreshItemInInventory()
    }

    fun enableSpeedUpgrade() {
        ignoreSpeedLevel = false
    }

    private fun animate(player: Player) {
        player.addPotionEffect(PotionEffect(PotionEffectType.LEVITATION, LEVITATION_DURATION, 0))
        player.playSound(player.location, Sound.ENTITY_ELDER_GUARDIAN_AMBIENT, SOUND_VOLUME, 1f)
        val scheduler = ServiceContainer[TaskScheduler::class.java]
        Circle(Particle.DUST, AnimationTarget(player), scheduler.plugin)
            .setColor(Color.FUCHSIA)
            .setDelay(PARTICLE_DELAY)
            .setRadius(PARTICLE_RADIUS)
            .setRotationCount(PARTICLE_ROTATION)
            .draw()
        scheduler.runTimer(ALTITUDE_SOUND_DELAY, 0, 0) {
            player.playSound(player.location, Sound.BLOCK_END_PORTAL_SPAWN, SOUND_VOLUME, 1f)
        }
    }

    fun synergicPropulsion(player: Player) {
        if (currentCharges <= 0) {
            player.sendMessage(ELYTRA_ERROR + "Vous n'avez plus de boost disponible, patientez quelques instants.")
            player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BASS, 1f, LOW_PITCH)
            return
        }
        player.sendMessage(ELYTRA_SUCCESS + "Décollage imminent !")
        val scheduler = ServiceContainer[TaskScheduler::class.java]
        Concentration(scheduler.plugin).setParticle(Particle.DUST)
            .setColor(Color.FUCHSIA)
            .setTarget(AnimationTarget(player))
            .setRadius(PROPULSION_RADIUS)
            .setCount(PROPULSION_COUNT)
            .build()
            .draw()
        player.playSound(player.location, Sound.ENTITY_ELDER_GUARDIAN_AMBIENT, SOUND_VOLUME, 1f)
        scheduler.runTimer(FIRST_TIMER_DELAY, 0, 0) {
            if (!player.isOnline || !isInvoked) return@runTimer
            player.velocity = Vector(0, 2, 0)
            player.playSound(player.location, Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST_FAR, FIREWORK_SOUND_VOLUME, 1f)
            scheduler.runTimer(SECOND_TIMER_DELAY, 0, 0) {
                if (player.isOnline && isInvoked) {
                    player.velocity = player.location.direction
                    player.isGliding = true
                    player.playSound(
                        player.location,
                        Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST_FAR, FIREWORK_SOUND_VOLUME, 1f
                    )
                }
            }
            currentCharges--
            saveYaml()
        }
    }

    private val effectiveSpeedLevel: Int get() = if (ignoreSpeedLevel) 1 else speedLevel

    override fun use(event: PlayerInteractEvent) {
        // Nothing
    }

    override fun useInInventory(event: InventoryClickEvent) {
        // Nothing
    }

    private fun refreshItemInInventory() {
        updateItem(true)
    }

    private fun saveYaml() {
        ServiceContainer[EquipmentService::class.java]
            .saveEquipment(ServiceContainer[EquipmentService::class.java].getEquipment(playerUUID))
    }

    companion object {
        private var lastVelocity: Double = 0.0
        private var isManuallyAccelerating = false

        fun getBoostCount(level: Int): Int {
            return if (level in 0..MAX_BOOST_LEVEL) {
                BOOST_BASE + level * BOOST_LEVEL_MULTIPLIER
            } else BOOST_BASE
        }

        fun getBaseRecoveryTime(level: Int): Long {
            return MAX_RECOVERY_TIME - (RECOVERY_DECREASE_PER_LEVEL * level)
        }

        fun getRecoveryTime(level: Int, player: Player): Long {
            val baseTime = getBaseRecoveryTime(level)
            val currentVelocity = player.velocity.length()
            val acceleratingPassively = player.isGliding && currentVelocity > lastVelocity && !isManuallyAccelerating
            lastVelocity = currentVelocity
            return if (!player.isGliding || acceleratingPassively) {
                (baseTime / 2)
            } else {
                baseTime
            }
        }

    }

}