package bromodachi.urlshortner.repository

import bromodachi.urlshortner.LONG_URL
import bromodachi.urlshortner.SHORT_URL
import bromodachi.urlshortner.exceptions.DuplicateException
import bromodachi.urlshortner.service.UrlServiceImpl
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest
import org.springframework.context.annotation.Import
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer

@JdbcTest
@Import(DBUrlRepository::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DbUrlRepositoryTest {
    @Autowired
    lateinit var jdbctemplate: JdbcTemplate

    @Autowired
    lateinit var urlServiceImpl: UrlRepository

    @BeforeEach
    fun cleanup() {
        jdbctemplate.execute("TRUNCATE urls")
    }

    @Test
    fun successfullyInsertShortId() {
        Assertions.assertTrue(
            urlServiceImpl.insert("google.com", "12345")
        )
    }

    @Test
    fun duplicateLongUrl() {
        Assertions.assertTrue(
            urlServiceImpl.insert("google.com", "12345")
        )
        val exception = Assertions.assertThrows(DuplicateException::class.java) {
            urlServiceImpl.insert("google.com", "12335")
        }
        Assertions.assertEquals(LONG_URL, exception.field)
    }

    @Test
    fun duplicateShortUrl() {
        Assertions.assertTrue(
            urlServiceImpl.insert("google.com", "12345")
        )
        val exception = Assertions.assertThrows(DuplicateException::class.java) {
            urlServiceImpl.insert("bob.com", "12345")
        }
        Assertions.assertEquals(SHORT_URL, exception.field)
    }

    companion object {
        @JvmStatic
        var postgres: PostgreSQLContainer<*> = PostgreSQLContainer<Nothing>(
            "postgres:16-alpine"
        )

        @JvmStatic
        @BeforeAll
        fun beforeAll(): Unit {
            postgres.start()
        }

        @JvmStatic
        @AfterAll
        fun afterAll(): Unit {
            postgres.start()
        }

        @DynamicPropertySource
        @JvmStatic
        fun configureProperties(registry: DynamicPropertyRegistry): Unit {
            registry.add("spring.datasource.url", postgres::getJdbcUrl);
            registry.add("spring.datasource.username", postgres::getUsername);
            registry.add("spring.datasource.password", postgres::getPassword);
            registry.add("spring.datasource.driver-class-name") { "org.postgresql.Driver" };
        }


    }
}