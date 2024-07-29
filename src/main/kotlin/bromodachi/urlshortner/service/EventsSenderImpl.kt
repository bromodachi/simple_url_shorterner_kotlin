package bromodachi.urlshortner.service

import bromodachi.urlshortner.dto.request.EventQueryDto
import bromodachi.urlshortner.dto.response.EventCountResponseDto
import bromodachi.urlshortner.network.EventClient
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

@Service
class EventsSenderImpl(
    private val eventsClient: EventClient
): EventsSender {
    override fun sendEvent(event: EventsSender.EventType) {
        logger.info { "Sending event: $event" }
        eventsClient.createEvent(event.toEventCreationDto())
    }

    override fun getEventCount(query: EventQueryDto): EventCountResponseDto =
        eventsClient.getEventCount(query)


    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
