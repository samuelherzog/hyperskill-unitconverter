package converter.conversion

import converter.measurements.ConvertibleUnit

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