package bromodachi.urlshortner.service

import bromodachi.urlshortner.LONG_URL
import bromodachi.urlshortner.SHORT_URL
import bromodachi.urlshortner.exceptions.DuplicateException
import bromodachi.urlshortner.exceptions.InternalServerError
import bromodachi.urlshortner.repository.UrlRepository
import bromodachi.urlshortner.utils.UrlShortener
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class UrlServiceTest {
    class InMemoryUrlRepository(
        private val base62ToLongUrl: MutableMap<String, String> = ConcurrentHashMap<String, String>(),
        private val longUrlToBase62: MutableMap<String, String> = ConcurrentHashMap<String, String>(),
    ): UrlRepository {

    private val reentryLock = ReentrantLock()

    override fun insert(longUrl: String, base62String: String): Boolean {
        if (longUrlToBase62.containsKey(longUrl)) {
            throw DuplicateException(field = LONG_URL)
        }
        else if (base62ToLongUrl.containsKey(base62String)) {
            throw DuplicateException(field = SHORT_URL)
        }
        reentryLock.withLock {
            base62ToLongUrl[base62String] = longUrl
            longUrlToBase62[longUrl] = base62String
        }
        return true
    }

    override fun getLongUrl(base62String: String): String? =
        base62ToLongUrl[base62String]

        override fun getShortUrl(longUrl: String): String? {
            return longUrlToBase62[longUrl]
        }

    }

    @Test
    fun successfullyCreated() {
        val urlService = UrlServiceImpl(InMemoryUrlRepository())
        val id = urlService.createUrl("https://kotlinlang.org/")
        Assertions.assertTrue(id.isNotBlank())
    }

    @Test
    fun duplicateUrl() {
        val urlService = UrlServiceImpl(InMemoryUrlRepository())
        val firstCreation = urlService.createUrl("https://kotlinlang.org/")
        val secondCreation = urlService.createUrl("https://kotlinlang.org/")
        Assertions.assertEquals(firstCreation, secondCreation)
    }

    @Test
    fun shortIdDuplicates() {
        val urlService = UrlServiceImpl(InMemoryUrlRepository(), object : UrlShortener {
            override fun encode(n: Long): String {
                return "6JaY2"
            }
            override fun decode(shortURL: String): Long {
                TODO("Not yet implemented")
            }
        })
        urlService.createUrl("https://kotlinlang.org/")
        val message = Assertions.assertThrows(InternalServerError::class.java) {
            urlService.createUrl("https://google.com")
        }
        Assertions.assertEquals("Couldn't create an id for https://google.com", message.message)
    }
}