package bromodachi.urlshortner.service

import bromodachi.urlshortner.dto.response.EventCountResponseDto

interface UrlService {
    fun createUrl(url: String): String
    fun getLongUrl(shortUrl: String): String
    fun getClicks(shortUrl: String): EventCountResponseDto
}