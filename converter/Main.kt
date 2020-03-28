package converter

import java.lang.Exception

interface UnitDomain {
    fun checkDomain(value: Double) {
        if (!inDomain(value)) throw domainException
    }

    fun inDomain(value: Double): Boolean
    fun getDomainError() : String
    val domainException get() = UnitDomainException(getDomainError())
}

interface FullDomain: UnitDomain {
    override fun inDomain(value: Double) = true
    override fun getDomainError() = ""
}

interface LowerBoundDomain : UnitDomain {
    val lowerBound: Double
    override fun inDomain(value: Double) = value >= lowerBound
}

interface PositiveDomain: LowerBoundDomain {
    override val lowerBound: Double
        get() = 0.0
}

interface LengthDomain: PositiveDomain {
    override fun getDomainError() = "Length shouldn't be negative"
}

interface MassDomain: PositiveDomain {
    override fun getDomainError() = "Weight shouldn't be negative"
}

interface KelvinDomain: PositiveDomain {
    override fun getDomainError() = "This temperature shouldn't be negative"
}

interface CelsiusDomain: LowerBoundDomain {
    override val lowerBound: Double
        get() = -237.15
    override fun getDomainError() = "This temperature can't be so low"
}

interface FahrenheitDomain: LowerBoundDomain {
    override val lowerBound: Double
        get() = -459.67
    override fun getDomainError() = "This temperature can't be so low"
}

class UnitDomainException(message: String) : Exception(message)

interface Convertible {
    fun toBase(value: Double): Double
    fun fromBase(value: Double): Double
}

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

sealed class LengthUnit(
        unit: String,
        singularName: String,
        pluralName: String,
        scale: Double
) : DirectScalingBaseUnit(unit, singularName, pluralName, scale), LengthDomain
{
    object METER: LengthUnit("m", "meter", "meters", 1.0)
    object MILE: LengthUnit("mi", "mile", "miles", 1609.35)
    object YARD: LengthUnit("yd", "yard", "yards", 0.9144)
    object FOOT: LengthUnit("ft", "foot", "feet", 0.3048)
    object INCH: LengthUnit("in", "inch", "inches", 0.0254)
}

sealed class MassUnit(
        unit: String,
        singularName: String,
        pluralName: String,
        scale: Double
) : DirectScalingBaseUnit(unit, singularName, pluralName, scale), MassDomain
{
    object GRAM: MassUnit("g", "gram", "grams", 1.0)
    object POUND: MassUnit("lb", "pound", "pounds", 453.592)
    object OUNCE: MassUnit("oz", "ounce", "ounces", 28.3495)
}

sealed class TempUnit(
        unit: String,
        singularName: String,
        pluralName: String,
        synonyms: List<String> = emptyList(),
        inBaseValues: (Double) -> Double = { it -> it },
        fromBaseValues: (Double) -> Double = { it -> it }
) : BaseUnit(unit, singularName, pluralName, synonyms, inBaseValues, fromBaseValues)
{
    object KELVIN : TempUnit("K", "Kelvin", "Kelvins"), FullDomain // should be KelvinDomain

    object CELSIUS : TempUnit(
            "C", "degree Celsius", "degrees Celsius",
            synonyms = listOf("dc", "Celsius"),
            inBaseValues = { it + 273.15 },
            fromBaseValues = { it - 273.15 }
    ), FullDomain // should be CelsiusDomain

    object FAHRENHEIT : TempUnit(
            "F", "degree Fahrenheit", "degrees Fahrenheit",
            synonyms = listOf("df", "Fahrenheit"),
            inBaseValues = { (it + 459.67) * 5/9 },
            fromBaseValues = { it * 9/5 - 459.67 }
    ), FullDomain // should be FahrenheitDomain
}

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

interface ConvertibleUnitCompanion {
    fun values(): List<ConvertibleUnit>
    fun hasName(name: String) = values().any { name in it.qualifiedNames() }
    fun fromName(name: String) = values().first { name in it.qualifiedNames() }
}

sealed class ConvertibleUnit(
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

sealed class Distance(lengthUnit: LengthUnit, siPrefix: SIPrefix = SIPrefix.ONE): ConvertibleUnit(lengthUnit, siPrefix) {
    object KM: Distance(LengthUnit.METER, SIPrefix.KILO)
    object  M: Distance(LengthUnit.METER)
    object CM: Distance(LengthUnit.METER, SIPrefix.CENTI)
    object MM: Distance(LengthUnit.METER, SIPrefix.MILLI)
    object MI: Distance(LengthUnit.MILE)
    object YD: Distance(LengthUnit.YARD)
    object FT: Distance(LengthUnit.FOOT)
    object IN: Distance(LengthUnit.INCH)

    override fun isConvertibleTo(target: ConvertibleUnit) =
            target in values()

    companion object : ConvertibleUnitCompanion {
        override fun values() = listOf(KM, M, CM, MM, MI, YD, FT, IN)
    }
}

sealed class Mass(massUnit: MassUnit, siPrefix: SIPrefix = SIPrefix.ONE): ConvertibleUnit(massUnit, siPrefix) {
    object G: Mass(MassUnit.GRAM)
    object KG: Mass(MassUnit.GRAM, SIPrefix.KILO)
    object MG: Mass(MassUnit.GRAM, SIPrefix.MILLI)
    object LB: Mass(MassUnit.POUND)
    object OZ: Mass(MassUnit.OUNCE)

    override fun isConvertibleTo(target: ConvertibleUnit) =
            target in values()

    companion object : ConvertibleUnitCompanion {
        override fun values() = listOf(G, KG, MG, LB, OZ)
    }
}

sealed class Temperature(tempUnit: TempUnit) : ConvertibleUnit(tempUnit) {
    object C: Temperature(TempUnit.CELSIUS)
    object K: Temperature(TempUnit.KELVIN)
    object F: Temperature(TempUnit.FAHRENHEIT)

    override fun isConvertibleTo(target: ConvertibleUnit) =
            target in values()

    companion object : ConvertibleUnitCompanion {
        override fun values() = listOf(C, K, F)
    }
}

class ConversionException(val base: ConvertibleUnit, val target: ConvertibleUnit) : Exception()

class ConvertibleValue(private val scalar: Double, private val unit: ConvertibleUnit)
{
    constructor(scalar: String, rawUnit: String) :
            this(scalar.toDouble(), ConvertibleUnit.fromName(rawUnit))

    init {
        unit.checkDomain(scalar)
    }

    private val isMultiple = (scalar != 1.0)
    private val scaledToBase = unit.toBase(scalar)

    fun convertTo(targetUnit: ConvertibleUnit): ConvertibleValue {
        if (!unit.isConvertibleTo(targetUnit)) {
            throw ConversionException(unit, targetUnit)
        }
        return if (targetUnit == unit) this
        else ConvertibleValue(targetUnit.fromBase(scaledToBase), targetUnit)
    }

    override fun toString() = "$scalar ${unit.pluralize(isMultiple)}"
}

fun main() {
    while (true) {
        print("Enter what you want to convert (or exit): ")
        val input = readLine()!!.trim().toLowerCase()

        if (input == "exit") break

        try {
            val (baseScalar, baseUnit, _, rawTargetUnit) =
                    input.replace("degree[s]? ".toRegex(), "").split(" ")

            val convertible = ConvertibleValue(baseScalar, baseUnit)
            val targetUnit = ConvertibleUnit.fromName(rawTargetUnit)
            val target = convertible.convertTo(targetUnit)

            println("$convertible is $target")

        } catch (e: UnitDomainException) {
            println(e.message)
        } catch (e: ConversionException) {
            println("Conversion from ${e.base} to ${e.target} is impossible")
        } catch (e: Throwable) {
            println("Parse error.")
        }
        println()
    }
}
