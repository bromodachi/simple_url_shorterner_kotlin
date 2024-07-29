package bromodachi.urlshortner.utils

import kotlin.math.pow


class UrlShortenerImpl: UrlShortener {
    // base 62
    private val digitChars = charArrayOf(
        '0','1','2','3','4','5','6','7','8','9',
        'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',
        'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'
    )

    private val BASE = digitChars.size

    @Suppress("NAME_SHADOWING")
    override fun encode(n: Long): String {
        if (n <= 0) {
            throw IllegalStateException("n must be positive: $n")
        }
        return buildString {
            var n = n
            while (n > 0) {
                append(digitChars[(n.mod(BASE))])
                n/= BASE;
            }
        }.reversed()
    }

    override fun decode(shortURL: String): Long {
        var result = 0L
        val length: Int = shortURL.length
        val base = BASE.toDouble()
        for (i in 0 until length) {
            result += base.pow(i).toLong() * digitChars.indexOf(shortURL[length - i - 1])
        }
        return result
    }
}