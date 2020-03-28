package converter.measurements

import converter.units.TemperatureUnit

sealed class Temperature(temperatureUnit: TemperatureUnit) : ConvertibleUnit(temperatureUnit) {
    object C: Temperature(TemperatureUnit.CELSIUS)
    object K: Temperature(TemperatureUnit.KELVIN)
    object F: Temperature(TemperatureUnit.FAHRENHEIT)

    override fun isConvertibleTo(target: ConvertibleUnit) =
            target in values()

    companion object : ConvertibleUnitCompanion {
        override fun values() = listOf(C, K, F)
    }
}
