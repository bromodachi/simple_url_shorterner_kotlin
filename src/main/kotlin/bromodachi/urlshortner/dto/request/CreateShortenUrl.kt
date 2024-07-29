package bromodachi.urlshortner.dto.request

import org.hibernate.validator.constraints.URL

data class CreateShortenUrl(
    @field:URL
    val url: String
)
