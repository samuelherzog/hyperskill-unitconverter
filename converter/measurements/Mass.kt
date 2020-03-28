package converter.measurements

import converter.units.MassUnit
import converter.units.SIPrefix

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
