package onl.tesseract.lib.repository

import org.bukkit.configuration.file.YamlConfiguration
import org.jetbrains.annotations.Contract
import java.io.File

interface ReadRepository<T, ID> {

    /**
     * Find an entity by its id
     * @return The entity mapped to this id, null if not found
     */
    fun getById(id: ID): T?

    /**
     * @return The ID mapped to this entity
     */
    fun idOf(entity: T): ID
}

/**
 * Abstract persistence layer to read and write entities from a data store
 */
interface Repository<T, ID> : ReadRepository<T, ID> {

    /**
     * Persist the entity
     */
    fun save(entity: T): T
}

interface RepositoryCK<T, IDA, IDB> : Repository<T, RepositoryCK.CompositeKey<IDA, IDB>> {

    fun getById(ida: IDA, idb: IDB): T?

    override fun getById(id: CompositeKey<IDA, IDB>): T? = getById(id.keyA, id.keyB)

    data class CompositeKey<IDA, IDB>(val keyA: IDA, val keyB: IDB)
}

/**
 * Repository with cache on read and writes
 */
abstract class RepositoryWithCache<T, ID> : Repository<T, ID> {

    protected val cache: MutableMap<ID, T> = mutableMapOf()

    final override fun getById(id: ID): T? {
        return cache[id] ?: cache(id, read(id))
    }

    override fun save(entity: T): T {
        val saved = write(entity)
        cache(idOf(entity), entity)
        return saved
    }

    abstract fun read(id: ID): T?

    abstract fun write(entity: T): T

    @Contract(pure = true, value = "(_, null) -> null, (_, !null) -> !null")
    protected fun cache(id: ID, t: T?): T? {
        if (t != null)
            cache[id] = t
        else
            cache.remove(id)
        return t
    }

    fun evict(id: ID) {
        cache.remove(id)
    }

    fun evictAll() {
        cache.clear()
    }
}

abstract class YamlFolderRepository<T, ID>(val folderPath: String) : RepositoryWithCache<T, ID>() {

    private var allFetched: Boolean = false

    private fun readAll(): Collection<T> {
        val folder = File(folderPath)
        if (!folder.exists()) return listOf()
        val entities = folder.listFiles { file -> file.extension == "yml" }
                ?.mapNotNull { YamlConfiguration.loadConfiguration(it) }
                ?.map { this.parse(it) }
                ?: listOf()
        entities.forEach { cache(idOf(it), it) }
        allFetched = true
        return entities
    }

    fun getAll(): Collection<T> {
        return if (allFetched) cache.values else readAll()
    }

    override fun read(id: ID): T? {
        val file = File("$folderPath/$id.yml")
        return if (file.exists()) parse(YamlConfiguration.loadConfiguration(file)) else null
    }

    override fun write(entity: T): T {
        val file = File("$folderPath/${idOf(entity)}.yml")
        serialize(entity).save(file)
        return entity
    }

    protected open fun fileFor(id: ID): File {
        return File("$folderPath/$id.yml")
    }

    abstract fun parse(yaml: YamlConfiguration): T

    abstract fun serialize(entity: T): YamlConfiguration
}