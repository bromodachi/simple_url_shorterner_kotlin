package bromodachi.urlshortner.dto.response

import java.util.Date

data class ErrorResponseDto(
    val timestamp: Date,
    val errorCode: String,
    val message: String,
    val description: String
)