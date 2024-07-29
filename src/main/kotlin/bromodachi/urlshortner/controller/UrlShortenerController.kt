package bromodachi.urlshortner.controller

import bromodachi.urlshortner.dto.request.CreateShortenUrl
import bromodachi.urlshortner.dto.response.EventCountResponseDto
import bromodachi.urlshortner.exceptions.BadRequestException
import bromodachi.urlshortner.service.UrlService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.view.RedirectView

@RestController
class UrlShortenerController(
    val urlService:UrlService
){

    // TODO: Use data class, change to POST
    @PostMapping("shorten")
    fun shorten(@RequestBody @Validated url: CreateShortenUrl): Map<String, String> {
        if (url.url.trim().isEmpty()) {
            throw BadRequestException(message = "url must but be empty")
        }
        val id = urlService.createUrl(url.url)
        logger.info { "successfully created $url for $id" }
        return mapOf(
            "originalUrl" to url.url,
            "url" to "http://localhost:8080/$id"
        )
    }

    @RequestMapping("/{shortUrl}")
    fun mapUrl(@PathVariable shortUrl: String): RedirectView {
        if (shortUrl.trim().isEmpty()) {
            throw BadRequestException(message = "url must but be empty")
        }
        val longUrl = urlService.getLongUrl(shortUrl)
        logger.info { "successfully retrieved $longUrl for $shortUrl" }
        return RedirectView().apply {
            url = longUrl
        }
    }

    @GetMapping("/{shortUrl}/clicks")
    fun getClicks(@PathVariable shortUrl: String): EventCountResponseDto {
        return urlService.getClicks(shortUrl)
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}