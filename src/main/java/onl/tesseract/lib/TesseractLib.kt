package onl.tesseract.lib

import onl.tesseract.lib.chat.ChatEntryService
import onl.tesseract.lib.equipment.EquipmentService
import onl.tesseract.lib.event.EventService
import onl.tesseract.lib.persistantcontainer.NamedspacedKeyProvider
import onl.tesseract.lib.persistence.yaml.equipment.EquipmentYamlRepository
import onl.tesseract.lib.profile.PlayerProfileService
import onl.tesseract.lib.service.PluginService
import onl.tesseract.lib.service.ServiceContainer
import onl.tesseract.lib.task.TaskScheduler
import org.bukkit.plugin.Plugin

object TesseractLib {

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
        equipmentService.registerEventHandler(plugin)
        container.registerService(PlayerProfileService::class.java, PlayerProfileService(taskScheduler))
        val chatEntryService = container.registerService(ChatEntryService::class.java, ChatEntryService(taskScheduler))
        pluginService.registerEventListener(chatEntryService)
    }
}