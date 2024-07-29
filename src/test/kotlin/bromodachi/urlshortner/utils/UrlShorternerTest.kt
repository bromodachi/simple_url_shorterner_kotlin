package bromodachi.urlshortner.utils

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class UrlShorternerTest {
    val urlShortener = UrlShortenerImpl()

    @Test
    fun testUrlShortner() {
        val encodedString = urlShortener.encode(14_776_337)
        val decoded = urlShortener.decode(encodedString)
        Assertions.assertEquals(14_776_337, decoded)
    }

    @Test
    fun hardCodedValue() {
        val decoded = urlShortener.decode("6JaY2")
        Assertions.assertEquals(99424938, decoded)
    }

    @Test
    fun numberIsNegative() {
        Assertions.assertThrows(IllegalStateException::class.java) {
            urlShortener.encode(-1)
        }
    }
}