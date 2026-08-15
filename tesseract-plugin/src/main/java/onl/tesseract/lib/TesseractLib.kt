package onl.tesseract.lib

import onl.tesseract.lib.chat.ChatEntryService
import onl.tesseract.lib.chat.tag.TagEventHandler
import onl.tesseract.lib.command.EquipmentCommand
import onl.tesseract.lib.command.InventoryCommand
import onl.tesseract.lib.command.LogLevelCommand
import onl.tesseract.lib.command.TranslationCommand
import onl.tesseract.lib.equipment.EquipmentService
import onl.tesseract.lib.event.EventService
import onl.tesseract.lib.inventory.InventoryInstanceEventHandler
import onl.tesseract.lib.inventory.InventoryInstanceManager
import onl.tesseract.lib.menu.MenuService
import onl.tesseract.lib.persistantcontainer.NamedspacedKeyProvider
import onl.tesseract.lib.persistence.yaml.equipment.EquipmentYamlRepository
import onl.tesseract.lib.profile.PlayerProfileService
import onl.tesseract.lib.service.PluginService
import onl.tesseract.lib.service.ServiceContainer
import onl.tesseract.lib.task.TaskScheduler
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin

object TesseractLib {

    fun loadInventoryConfigurations() {
        InventoryInstanceManager.loadConfigurations()
    }

    fun registerOnEnable(plugin: JavaPlugin) {
        registerDefaultServices(plugin)
        registerEventHandlers(plugin)
        registerCommands(plugin)
    }

    fun registerCommands(plugin: JavaPlugin) {
//        plugin.getCommand("animation")!!.setExecutor(Animation(plugin))
//        plugin.getCommand("animation")!!.setTabCompleter(Animation(plugin))
        plugin.getCommand("equipment")!!.setExecutor(EquipmentCommand())
        plugin.getCommand("inventory")!!.setExecutor(InventoryCommand())
        plugin.getCommand("inventory")!!.setTabCompleter(InventoryCommand())
        plugin.getCommand("translation")!!
                .setExecutor(TranslationCommand())
        LogLevelCommand().register(plugin, "log")
    }

    fun registerEventHandlers(plugin: JavaPlugin) {
        plugin.server.pluginManager.registerEvents(TagEventHandler(), plugin)
        plugin.server.pluginManager.registerEvents(InventoryInstanceEventHandler(),plugin)
    }

    fun registerDefaultServices(plugin: Plugin) {
        val container = ServiceContainer.getInstance()
        val pluginService = container.registerService(PluginService::class.java, PluginService(plugin))
        val taskScheduler = container.registerService(TaskScheduler::class.java, TaskScheduler(plugin))
        val namedspacedKeyProvider =
            container.registerService(NamedspacedKeyProvider::class.java, NamedspacedKeyProvider(plugin))
        val eventService = container.registerService(EventService::class.java, EventService(plugin))
        val equipmentService = container.registerService(
            EquipmentService::class.java,
            EquipmentService(EquipmentYamlRepository, namedspacedKeyProvider, eventService)
        )
        container.registerService(MenuService::class.java, MenuService(pluginService,eventService));
        equipmentService.registerEventHandler(plugin)
        container.registerService(PlayerProfileService::class.java, PlayerProfileService())
        val chatEntryService = container.registerService(ChatEntryService::class.java, ChatEntryService(taskScheduler))
        pluginService.registerEventListener(chatEntryService)

        pluginService.registerEventListener(TagEventHandler())
    }
}