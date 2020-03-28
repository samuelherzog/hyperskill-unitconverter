package converter.domain

interface MassDomain: PositiveDomain {
    override fun getDomainError() = "Weight shouldn't be negative"
}