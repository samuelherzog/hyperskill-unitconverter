package converter.conversion

import converter.measurements.ConvertibleUnit

class ConversionException(
        val base: ConvertibleUnit,
        val target: ConvertibleUnit
) : Exception()