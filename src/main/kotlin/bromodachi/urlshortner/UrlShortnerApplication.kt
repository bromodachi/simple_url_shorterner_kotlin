package bromodachi.urlshortner

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients
class UrlShortnerApplication

private val logger = KotlinLogging.logger {}
fun main(args: Array<String>) {
    logger.info { "starting application..." }
    runApplication<UrlShortnerApplication>(*args)
}
