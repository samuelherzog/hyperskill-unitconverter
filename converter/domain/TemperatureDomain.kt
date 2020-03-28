package converter.domain

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
