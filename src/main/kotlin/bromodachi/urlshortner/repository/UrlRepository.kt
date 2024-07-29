package bromodachi.urlshortner.repository

interface UrlRepository {
    fun insert(longUrl: String, base62String: String): Boolean
    fun getLongUrl(base62String: String): String?
    fun getShortUrl(longUrl: String): String?
}