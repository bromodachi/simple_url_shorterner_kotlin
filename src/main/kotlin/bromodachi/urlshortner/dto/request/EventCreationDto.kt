package bromodachi.urlshortner.dto.request

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class EventCreationDto(
    val id: String,
    val keyValue: List<EventKeyValue>
)
data class EventKeyValue(
    val key: String,
    val value: String? = null
)
