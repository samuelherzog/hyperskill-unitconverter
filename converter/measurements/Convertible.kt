package converter.measurements

interface Convertible {
    fun toBase(value: Double): Double
    fun fromBase(value: Double): Double
}
