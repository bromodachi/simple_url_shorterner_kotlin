package bromodachi.urlshortner.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming


@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class EventQueryDto(
    val from: Long,
    val to: Long,
    val id: String,
    val key: String,
    // TODO: Jackson not handling this properly, look into it.
    @field:JsonProperty("query_type")
    val query_type: String
)
