package bromodachi.urlshortner.config

import bromodachi.urlshortner.network.EventClient
import bromodachi.urlshortner.repository.UrlRepository
import bromodachi.urlshortner.service.EventsSenderImpl
import bromodachi.urlshortner.service.UrlService
import bromodachi.urlshortner.service.UrlServiceImpl
import bromodachi.urlshortner.utils.UrlShortenerImpl
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class BeanCreation {
    @Bean(name = ["UrlService"])
    @Primary
    fun urlService(
        @Autowired urlRepository: UrlRepository,
        @Autowired eventClient: EventClient,
        @Value("\${events-sender.enabled:false}") enableEventsSender: Boolean
    ): UrlService {
        logger.info { "enabling enableEventsSender: $enableEventsSender" }
        return UrlServiceImpl(urlRepository, UrlShortenerImpl(), if (enableEventsSender) EventsSenderImpl(eventClient) else null)
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}