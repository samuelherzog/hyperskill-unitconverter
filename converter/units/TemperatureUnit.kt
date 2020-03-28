package converter.units

import converter.domain.KelvinDomain
import converter.domain.CelsiusDomain
import converter.domain.FahrenheitDomain


sealed class TemperatureUnit(
        unit: String,
        singularName: String,
        pluralName: String,
        synonyms: List<String> = emptyList(),
        inBaseValues: (Double) -> Double = { it -> it },
        fromBaseValues: (Double) -> Double = { it -> it }
) : BaseUnit(unit, singularName, pluralName, synonyms, inBaseValues, fromBaseValues)
{
    object KELVIN : TemperatureUnit("K", "Kelvin", "Kelvins"), KelvinDomain

    object CELSIUS : TemperatureUnit(
            "C", "degree Celsius", "degrees Celsius",
            synonyms = listOf("dc", "Celsius"),
            inBaseValues = { it + 273.15 },
            fromBaseValues = { it - 273.15 }
    ), CelsiusDomain

    object FAHRENHEIT : TemperatureUnit(
            "F", "degree Fahrenheit", "degrees Fahrenheit",
            synonyms = listOf("df", "Fahrenheit"),
            inBaseValues = { (it + 459.67) * 5/9 },
            fromBaseValues = { it * 9/5 - 459.67 }
    ), FahrenheitDomain
}