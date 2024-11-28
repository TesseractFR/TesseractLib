package onl.tesseract.lib.logger

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.event.Level

class LoggerFactory {

    companion object {
        private val loggers: MutableMap<String, LoggerWrapper> = mutableMapOf()
        @JvmStatic
        var defaultLevel: Level = Level.INFO

        @JvmStatic
        fun getLogger(forClass: Class<*>): Logger = getLogger(forClass.name)

        @JvmStatic
        fun getLogger(name: String): Logger {
            if (name in loggers) return loggers[name]!!
            val newLogger = LoggerWrapper(LoggerFactory.getLogger(name), defaultLevel)
            loggers[name] = newLogger
            return newLogger
        }

        @JvmStatic
        fun setLogLevel(forClass: Class<*>, level: Level) {
            (getLogger(forClass) as LoggerWrapper).setLevel(level)
        }

        @JvmStatic
        fun setLogLevel(name: String, level: Level) {
            (getLogger(name) as LoggerWrapper).setLevel(level)
        }

        @JvmStatic
        fun getKnownLoggers(): Collection<String> = loggers.keys
    }
}

fun logger(forClass: Class<*>): Logger = onl.tesseract.lib.logger.LoggerFactory.getLogger(forClass)