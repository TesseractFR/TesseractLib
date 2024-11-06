package onl.tesseract.lib

typealias Tick = Long

class Ticks {
    companion object {
        fun ofSeconds(seconds: Long): Tick = seconds * 20
    }
}
