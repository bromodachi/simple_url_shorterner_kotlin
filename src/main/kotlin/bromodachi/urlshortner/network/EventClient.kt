package bromodachi.urlshortner.network

import bromodachi.urlshortner.dto.request.EventCreationDto
import bromodachi.urlshortner.dto.request.EventQueryDto
import bromodachi.urlshortner.dto.response.EventCountResponseDto
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.cloud.openfeign.SpringQueryMap
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient("event-client", url ="http://localhost:8000/")
interface EventClient {
    @GetMapping("/event")
    fun getEventCount(@SpringQueryMap eventQueryDto: EventQueryDto): EventCountResponseDto

    @PostMapping("/event")
    fun createEvent(@RequestBody events: EventCreationDto): ResponseEntity<Unit>
}