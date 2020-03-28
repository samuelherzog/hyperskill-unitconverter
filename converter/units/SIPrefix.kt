package converter.units

sealed class SIPrefix(
        private val prefix: String,
        private val name: String,
        private val scale: Double
)
{
    object ONE : SIPrefix("", "", 1.0)
    object KILO : SIPrefix("k", "kilo", 1000.0)
    object CENTI : SIPrefix("c", "centi", 0.01)
    object MILLI : SIPrefix("m", "milli", 0.001)

    fun denote(scaledValue: Double) = scaledValue / scale

    operator fun invoke(baseValue: Double) = baseValue * scale

    fun qualifiers() = listOf(prefix, name).distinct()
    override fun toString() = name
}
