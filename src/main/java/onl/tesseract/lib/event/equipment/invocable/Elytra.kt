package onl.tesseract.lib.event.equipment.invocable

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import onl.tesseract.lib.animation.AnimationTarget
import onl.tesseract.lib.animation.Circle
import onl.tesseract.lib.animation.Concentration
import onl.tesseract.lib.equipment.EquipmentService
import onl.tesseract.lib.equipment.Invocable
import onl.tesseract.lib.service.PluginService
import onl.tesseract.lib.service.ServiceContainer
import onl.tesseract.lib.task.TaskScheduler
import onl.tesseract.lib.menu.ItemBuilder
import onl.tesseract.lib.util.Util
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
import org.bukkit.event.player.PlayerToggleSneakEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitTask
import org.bukkit.util.Vector
import java.util.*

class Elytra(playerUUID: UUID, invoked: Boolean, handSlot: Int) : Invocable(playerUUID, invoked, handSlot), Listener {
    private var accelerateTask: BukkitTask? = null
    private var actionBarTask: BukkitTask? = null

    var autoGlide: Boolean = true
    var ignoreSpeedLevel: Boolean = false

    var protectionLevel: Int = 0
    var speedLevel: Int = 0
    var boostChargeLevel: Int = 0
    var recoveryLevel: Int = 0

    var currentCharges: Int = 0
    var rechargeProgress: Double = 0.0

    override val excludeOthers: Boolean = true
    override val slotType: EquipmentSlot = EquipmentSlot.CHEST
    override val uniqueName: String = "ELYTRA"

    override fun createItem(): ItemStack {
        val boostCount = getBoostCount(boostChargeLevel)
        val recoveryTimeSeconds = getRecoveryTime(recoveryLevel) / 1000
        return ItemBuilder(Material.ELYTRA)
            .name(Component.text("Flanc éthéré", NamedTextColor.GOLD))
            .lore()
            .append(Component.text("« Des ailes divines imprégnées de clairvoyance. »", NamedTextColor.DARK_PURPLE))
            .newline()
            .newline()
            .append(Component.text("Vitesse : ", NamedTextColor.DARK_AQUA))
            .append(Component.text(speedLevel.toString(), NamedTextColor.GOLD))
            .newline()
            .append(Component.text("Protection : ", NamedTextColor.DARK_AQUA))
            .append(Component.text(protectionLevel.toString(), NamedTextColor.GOLD))
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
                this.itemMeta = meta
            }
    }

    @EventHandler
    fun onAccelerate(event: PlayerToggleSneakEvent) {
        if (event.player.uniqueId != playerUUID) return

        val player = event.player

        if (event.isSneaking && isInvoked && player.isGliding && accelerateTask == null) {
            if (currentCharges <= 0) {
                player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.5f)
                return
            }

            accelerateTask = ServiceContainer.get(TaskScheduler::class.java).runTimer(0, 10, 0) { task ->
                val speed = if (player.location.world.name == "Event") 0 else effectiveSpeedLevel
                if (!player.isOnline || !player.isSneaking || !player.isGliding) {
                    task.cancel()
                    accelerateTask = null
                } else if (player.velocity.length() < (1.20 + (0.10 * (speed + 1)))) {
                    currentCharges--
                    player.velocity = player.velocity.add(player.location.direction.multiply(0.7))
                    player.world.playSound(player.location, Sound.ENTITY_ENDER_DRAGON_FLAP, SoundCategory.PLAYERS, 1f, 1f)
                }
                Unit
            }
            saveYaml()
        }
    }


    @EventHandler
    fun onFly(event: EntityToggleGlideEvent) {
        if (event.entityType != EntityType.PLAYER) return
        if (event.entity.uniqueId == playerUUID && isInvoked) {
            val player = event.entity as Player
            if (event.isGliding) displayActionBar(player)
            else if (autoGlide) autoGlide = true
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
            if (currentCharges < getBoostCount(boostChargeLevel)) {
                val rechargeTime = getRecoveryTime(recoveryLevel)
                rechargeProgress += (2.0 * 50 / rechargeTime)
                saveYaml()
                if (rechargeProgress >= 1.0) {
                    currentCharges++
                    rechargeProgress = 0.0
                }
            }
            val comp = Component.text()
            if (player.isGliding) {
                comp.append(Component.text("Vitesse: ", NamedTextColor.GRAY))
                    .append(Component.text((player.velocity.length() * 20).toInt(), NamedTextColor.AQUA))
                    .append(Component.text(" | ", NamedTextColor.DARK_GRAY))
                    .append(Component.text("Alt: "))
                    .append(Component.text(player.location.blockY, NamedTextColor.GREEN))
                    .append(Component.text(" | ", NamedTextColor.DARK_GRAY))
                    .append(Component.text("Distance: "))
                    .append(Component.text(player.location.distance(player.compassTarget).toInt(), NamedTextColor.YELLOW))
                    .append(Component.text(" | ", NamedTextColor.DARK_GRAY))
            }
            comp.append(Component.text("Charges de Boost: "))
                .append(Component.text("[$currentCharges/${getBoostCount(boostChargeLevel)}]", NamedTextColor.GOLD))
            if (currentCharges < getBoostCount(boostChargeLevel)) {
                comp.append(Component.text(" "))
                    .append(progressBarColored(rechargeProgress))
            }

            player.sendActionBar(comp)
        }
    }


    private fun progressBarColored(value: Double): Component {
        val bars = 10
        val filled = (value * bars).coerceAtMost(bars.toDouble()).toInt()
        val builder = Component.text()
        repeat(filled) {
            builder.append(Component.text("|", NamedTextColor.GREEN))
        }
        repeat(bars - filled) {
            builder.append(Component.text(".", NamedTextColor.RED))
        }
        return builder.build()
    }

    override fun onUninvoke(player: Player, manualUninvocation: Boolean) {
        ServiceContainer[PluginService::class.java].unregisterEventListener(this)
        actionBarTask?.cancel()
        actionBarTask = null
        if (manualUninvocation) animate(player)
    }

    override fun onInvoke(player: Player, manuelInvocation: Boolean) {
        ServiceContainer[PluginService::class.java].registerEventListener(this)
        if (!player.isOnGround && !player.isGliding) player.isGliding = true
        if (autoGlide) autoGlide = true
        if (currentCharges == 0) currentCharges = getBoostCount(boostChargeLevel)
        displayActionBar(player)
        if (manuelInvocation) animate(player)
    }


    private fun animate(player: Player) {
        player.addPotionEffect(PotionEffect(PotionEffectType.LEVITATION, 40, 0))
        player.playSound(player.location, Sound.ENTITY_ELDER_GUARDIAN_AMBIENT, 20f, 1f)
        val scheduler = ServiceContainer.get(TaskScheduler::class.java)
        Circle(Particle.DUST, AnimationTarget(player), scheduler.plugin)
            .setColor(Color.FUCHSIA)
            .setDelay(0.1f)
            .setRadius(1f)
            .setRotationCount(3f)
            .draw()
        scheduler.runTimer(40, 0, 0) {
            player.playSound(player.location, Sound.BLOCK_END_PORTAL_SPAWN, 20f, 1f)
            Unit
        }
    }

    fun synergicPropulsion(player: Player) {
        val scheduler = ServiceContainer.get(TaskScheduler::class.java)
        Concentration(scheduler.plugin).setParticle(Particle.DUST)
            .setColor(Color.FUCHSIA)
            .setTarget(AnimationTarget(player))
            .setRadius(2)
            .setCount(20)
            .build()
            .draw()
        player.playSound(player.location, Sound.ENTITY_ELDER_GUARDIAN_AMBIENT, 20f, 1f)
        scheduler.runTimer(30, 0, 0) {
            if (!player.isOnline || !isInvoked) return@runTimer Unit
            player.velocity = Vector(0, 2, 0)
            player.playSound(player.location, Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST_FAR, 150f, 1f)
            scheduler.runTimer(20, 0, 0) {
                if (player.isOnline && isInvoked) {
                    player.velocity = player.location.direction
                    player.isGliding = true
                    player.playSound(player.location, Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST_FAR, 150f, 1f)
                }
                Unit
            }
            Unit
        }
    }

    private val effectiveSpeedLevel: Int get() = if (ignoreSpeedLevel) 1 else speedLevel

    override fun use(event: PlayerInteractEvent) {}
    override fun useInInventory(event: InventoryClickEvent) {}

    fun refreshItemInInventory() {
        updateItem(true)
    }

    private fun saveYaml() {
        ServiceContainer[EquipmentService::class.java]
            .saveEquipment(ServiceContainer[EquipmentService::class.java].getEquipment(playerUUID))
    }


    companion object {
        private val boostCounts = listOf(5, 10, 25, 50, 75, 100, 150, 250, 500, 1000)
        private val recoveryTimes = listOf(60000L, 50000L, 40000L, 30000L, 25000L, 20000L, 15000L, 12000L,
            10000L, 8000L)

        fun getBoostCount(level: Int): Int = boostCounts.getOrNull(level) ?: 5
        fun getRecoveryTime(level: Int): Long = recoveryTimes.getOrNull(level) ?: 60000L
    }
}
