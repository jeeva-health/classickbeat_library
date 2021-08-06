package ai.heart.classickbeats.mapper

interface Mapper<I, O> {
    fun map(input: I): O
}