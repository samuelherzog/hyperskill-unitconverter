package converter.measurements

import converter.units.BaseUnit
import converter.units.NoUnit
import converter.units.SIPrefix
import converter.domain.UnitDomain

interface ConvertibleUnitCompanion {
    fun values(): List<ConvertibleUnit>
    fun hasName(name: String) = values().any { name in it.qualifiedNames() }
    fun fromName(name: String) = values().first { name in it.qualifiedNames() }
}

abstract class ConvertibleUnit(
        private val baseUnit: BaseUnit,
        private val siPrefix: SIPrefix = SIPrefix.ONE
) : Convertible, UnitDomain
{
    abstract fun isConvertibleTo(target: ConvertibleUnit) : Boolean

    override fun toBase(value: Double) = baseUnit.toBase(siPrefix(value))
    override fun fromBase(value: Double) = baseUnit.fromBase(siPrefix.denote(value))

    override fun inDomain(value: Double) = baseUnit.inDomain(siPrefix(value))
    override fun getDomainError() = baseUnit.getDomainError()

    fun qualifiedNames() : List<String> {
        return siPrefix.qualifiers().map { q ->
            baseUnit.names().map { "$q$it" }
        }.flatten()
    }

    override fun toString() = pluralize(true)
    fun pluralize(usePlural: Boolean = true) =
            "$siPrefix${baseUnit.pluralizeName(usePlural)}"

    companion object {
        fun fromName(name: String): ConvertibleUnit {
            return when {
                Distance.hasName(name) -> Distance.fromName(name)
                Mass.hasName(name) -> Mass.fromName(name)
                Temperature.hasName(name) -> Temperature.fromName(name)
                else -> UnknownUnit
            }
        }
    }
}

object UnknownUnit: ConvertibleUnit(NoUnit), ConvertibleUnitCompanion {
    override fun values() = listOf(UnknownUnit)
    override fun isConvertibleTo(target: ConvertibleUnit) = false
}