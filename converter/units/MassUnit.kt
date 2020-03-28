package converter.units

import converter.domain.MassDomain

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