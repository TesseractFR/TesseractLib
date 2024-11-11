package onl.tesseract.lib.repository

import org.jetbrains.annotations.Contract

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
    fun save(entity: T)
}

/**
 * Repository with cache on read and writes
 */
abstract class RepositoryWithCache<T, ID> : Repository<T, ID> {

    private val cache: MutableMap<ID, T> = mutableMapOf()

    final override fun getById(id: ID): T? {
        return cache[id] ?: read(id)
    }

    override fun save(entity: T) {
        write(entity)
        cache(idOf(entity), entity)
    }

    abstract fun read(id: ID): T?

    abstract fun write(entity: T)

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