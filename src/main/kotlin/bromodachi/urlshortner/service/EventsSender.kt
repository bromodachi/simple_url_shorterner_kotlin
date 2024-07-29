package bromodachi.urlshortner.service

import bromodachi.urlshortner.dto.request.EventCreationDto
import bromodachi.urlshortner.dto.request.EventKeyValue
import bromodachi.urlshortner.dto.request.EventQueryDto
import bromodachi.urlshortner.dto.response.EventCountResponseDto

interface EventsSender {
    sealed interface EventType {
        val id: String
        val key: String


        fun toEventCreationDto(): EventCreationDto {
            return EventCreationDto(
                id = this.id,
                keyValue = listOf(EventKeyValue(key = this.key))
            )
        }

        data class Clicked(
            override val id: String,
            override val key: String = CLICKED_KEY
        ): EventType

        data class Created(
            override val id: String,
            override val key: String = CREATED_KEY
        ): EventType

    }

    fun sendEvent(eventsSender: EventType)

    fun getEventCount(query: EventQueryDto): EventCountResponseDto

    companion object {
        const val CREATED_KEY = "created"
        const val CLICKED_KEY = "clicked"
    }
}