package converter.measurements

import converter.units.LengthUnit
import converter.units.SIPrefix

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
