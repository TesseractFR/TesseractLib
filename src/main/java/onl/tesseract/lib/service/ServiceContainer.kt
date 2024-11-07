package onl.tesseract.lib.service

class ServiceContainer {

    private val services: MutableMap<Class<*>, Any> = mutableMapOf()

    fun <S : Any, T : S> registerService(type: Class<S>, service: T) {
        services[type] = service
    }

    fun <T> getService(type: Class<T>): T {
        val service: Any = services[type] ?: throw IllegalArgumentException("$type not found")
        return service as? T ?: throw IllegalStateException("Service ${service.javaClass.name} incompatible with type ${type.name}")
    }

    companion object {
        private var INSTANCE: ServiceContainer? = null

        @JvmStatic
        fun getInstance(): ServiceContainer {
            if (INSTANCE == null) {
                INSTANCE = ServiceContainer()
            }
            return INSTANCE!!
        }

        @JvmStatic
        operator fun <T> get(type: Class<T>): T = getInstance().getService(type)
    }
}