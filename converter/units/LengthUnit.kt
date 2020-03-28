package converter.units

import converter.domain.LengthDomain

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