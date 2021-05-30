package ai.heart.classickbeats.shared.mapper

interface Mapper<I, O> {
    fun map(input: I): O
}