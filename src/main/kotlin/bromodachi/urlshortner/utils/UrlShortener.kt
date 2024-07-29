package bromodachi.urlshortner.utils

interface UrlShortener {
    fun encode(n: Long): String
    fun decode(shortURL: String): Long
}