package bromodachi.urlshortner.service

import bromodachi.urlshortner.LONG_URL
import bromodachi.urlshortner.SHORT_URL
import bromodachi.urlshortner.dto.request.EventQueryDto
import bromodachi.urlshortner.dto.response.EventCountResponseDto
import bromodachi.urlshortner.exceptions.BadRequestException
import bromodachi.urlshortner.exceptions.DuplicateException
import bromodachi.urlshortner.exceptions.InternalServerError
import bromodachi.urlshortner.utils.UrlShortenerImpl
import bromodachi.urlshortner.repository.UrlRepository
import bromodachi.urlshortner.utils.UrlShortener
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import kotlin.math.pow
import kotlin.random.Random

@Service
class UrlServiceImpl(
    val urlRepository: UrlRepository,
    val urlShortener: UrlShortener = UrlShortenerImpl(),
    val eventsSender: EventsSender? = null
): UrlService {

    override fun createUrl(url: String): String {
        val randomId = Random.nextLong((62.0.pow(4.0) + 1).toLong(), (62.0.pow(5.0)).toLong())
        var id = urlShortener.encode(randomId)
        for (i in 1..3) {
            try {
                if (urlRepository.insert(url, id)) {
                    eventsSender?.sendEvent(EventsSender.EventType.Created(id))
                    return id
                }
            } catch (e: DuplicateException) {
                logger.warn(e) { "received duplicate exception" }
                when (e.field) {
                    LONG_URL -> return urlRepository.getShortUrl(url) ?: throw InternalServerError(message = "Duplicate exception was thrown but url was not found.")
                    SHORT_URL -> {
                        logger.warn { "Duplicate id, will retry..." }
                        id = urlShortener.encode(randomId)
                        continue
                    }
                    else -> {
                        throw e
                    }
                }
            }
        }
        throw InternalServerError(message = "Couldn't create an id for $url")
    }

    override fun getLongUrl(shortUrl: String): String {
        val response =  urlRepository.getLongUrl(shortUrl) ?: throw BadRequestException(message = "url $shortUrl not found")
        eventsSender?.sendEvent(EventsSender.EventType.Clicked(shortUrl))
        return response
    }

    override fun getClicks(shortUrl: String): EventCountResponseDto {
        return eventsSender?.getEventCount(EventQueryDto(from = 0, to = Long.MAX_VALUE, id = shortUrl, key = EventsSender.CLICKED_KEY, query_type = "count"))
            ?: throw BadRequestException(message = "Eventsender is currently not enabled.")
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}