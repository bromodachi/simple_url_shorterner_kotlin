package bromodachi.urlshortner.repository

import bromodachi.urlshortner.LONG_URL
import bromodachi.urlshortner.SHORT_URL
import bromodachi.urlshortner.exceptions.DuplicateException
import bromodachi.urlshortner.exceptions.InternalServerError
import io.github.oshai.kotlinlogging.KotlinLogging

import jakarta.annotation.PostConstruct
import org.springframework.dao.DuplicateKeyException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import org.sqlite.SQLiteErrorCode
import org.sqlite.SQLiteException

@Repository
class DBUrlRepository(
    private val jdbctemplate: JdbcTemplate
): UrlRepository {

    @PostConstruct
    fun constructDB() {
        jdbctemplate.execute("CREATE TABLE IF NOT EXISTS urls (short_url varchar(32) Primary Key, long_url TEXT UNIQUE);")
    }

    private fun throwIfMatched(matchResult: MatchResult? ) {
        if (matchResult != null && matchResult.groupValues.size > 1) {
            when (matchResult.groupValues[1]) {
                LONG_URL -> throw DuplicateException(field = LONG_URL)
                SHORT_URL -> throw DuplicateException(field = SHORT_URL)
            }
        }
    }
    override fun insert(longUrl: String, base62String: String): Boolean {
        try {
            return jdbctemplate.update(
                """
                INSERT INTO urls (short_url, long_url) VALUES (?, ?)
            """.trimIndent(),
                base62String,
                longUrl
            ) >= 1
        } catch (e: Exception) {
            // SQLITE
            if (e.cause is SQLiteException) {
                val exception = e.cause as SQLiteException
                if (exception.resultCode == SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE) {
                    val matchResult = URL_FIELD.find(exception.message ?: "" )
                    throwIfMatched(matchResult)
                }
            }
            // POSTGRES
            else if (e is DuplicateKeyException) {
                logger.debug { "duplicate came here" }
                val result = POSTGRES_REGEX.find(e.message ?: "")
                logger.debug { "result: $result" }
                throwIfMatched(result)
            }
            logger.error(e) { "Unknown error. wtf" }
            throw InternalServerError(message = "Failed to create url for $longUrl with id $base62String")
        }
    }

    override fun getLongUrl(base62String: String): String? {
        return jdbctemplate.queryForList("""
            SELECT long_url FROM urls WHERE short_url = ? LIMIT 1
        """.trimIndent(),
        String::class.java,
        base62String
        ).let {
            if (it.isEmpty()) {
                return null
            } else {
                it.first()
            }
        }
    }

    override fun getShortUrl(longUrl: String): String? {
        return jdbctemplate.queryForList("""
            SELECT short_url FROM urls WHERE long_url = ? LIMIT 1
        """.trimIndent(),
            String::class.java,
            longUrl
        ).let {
            if (it.isEmpty()) {
                return null
            } else {
                it.first()
            }
        }
    }

    companion object {
        private val URL_FIELD = """urls\.(\w+)""".toRegex()
        private val POSTGRES_REGEX = """Detail: Key \((\w+)\)=\(.*\)""".toRegex()
        private val logger = KotlinLogging.logger {}
    }
}