package converter.units

import converter.measurements.Convertible
import converter.domain.FullDomain
import converter.domain.UnitDomain

abstract class BaseUnit(
        private val unit: String,
        private val singularName: String,
        private val pluralName: String,
        private val synonyms: List<String> = emptyList(),
        private val inBaseValues: (Double) -> Double = { it -> it },
        private val fromBaseValues: (Double) -> Double = { it -> it }
) : Convertible, UnitDomain
{
    override fun toString() = pluralizeName(true)
    override fun fromBase(value: Double) = fromBaseValues(value)
    override fun toBase(value: Double) = inBaseValues(value)

    fun pluralizeName(usePlural: Boolean) = if (usePlural) pluralName else singularName
    fun names() = (listOf(unit, singularName, pluralName) + synonyms).map { it.toLowerCase() }
}

abstract class DirectScalingBaseUnit(
        unit: String,
        singularName: String,
        pluralName: String,
        scale: Double
) : BaseUnit(
        unit, singularName, pluralName,
        inBaseValues = {it * scale}, fromBaseValues = {it / scale}
)

object NoUnit : BaseUnit("???", "???", "???"), FullDomain
