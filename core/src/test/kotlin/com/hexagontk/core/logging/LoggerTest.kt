package com.hexagontk.core.logging

import com.hexagontk.core.text.Ansi.RESET
import com.hexagontk.core.text.AnsiColor.BRIGHT_WHITE
import com.hexagontk.core.text.AnsiColor.RED_BG
import com.hexagontk.core.text.AnsiEffect.UNDERLINE
import com.hexagontk.core.urlOf
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.condition.DisabledInNativeImage
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import java.lang.System.Logger.Level.ERROR
import java.lang.System.Logger.Level.TRACE
import java.util.logging.LogManager
import kotlin.IllegalStateException
import kotlin.reflect.KClass
import kotlin.test.*

@TestInstance(PER_CLASS)
internal class LoggerTest {

    @BeforeAll fun setUp() {
        val configuration = urlOf("classpath:sample.properties")
        LogManager.getLogManager().readConfiguration(configuration.openStream())
    }

    @Suppress("RedundantExplicitType", "UNUSED_VARIABLE") // Ignored for examples generation
    @Test fun loggerUsage() {
        // logger
        val classLogger: Logger = Logger(Runtime::class) // Logger for the `Runtime` class
        val instanceLogger: Logger = Logger(this::class) // Logger for this instance's class

        logger.info {
            """
            You can add a quick log without declaring a Logger with
            'com.hexagontk.core.logging.logger'. It is a default logger created with a custom name
            (same as `Logger(LoggingManager.defaultLoggerName)`).
            """
        }

        classLogger.trace { "Message only evaluated if trace enabled" }
        classLogger.debug { "Message only evaluated if debug enabled" }
        classLogger.warn { "Message only evaluated if warn enabled" }
        classLogger.info { "Message only evaluated if info enabled" }

        val exception = IllegalStateException("Exception")
        classLogger.warn(exception) { "Warning with exception" }
        classLogger.error(exception) { "Error message with exception" }
        classLogger.warn(exception)
        classLogger.error(exception)
        classLogger.error { "Error without an exception" }
        // logger
    }

    /**
     * As the logger is only a facade, and it is hard to check outputs, the only check is that no
     * exceptions are thrown.
     */
    @Test fun `Messages are logged without errors using JUL`() {

        val logger = Logger(this::class)

        logger.trace { 42 }
        logger.debug { true }
        logger.info { 0.0 }
        logger.warn(RuntimeException())
        logger.error(RuntimeException())
        logger.warn { listOf(0, 1) }
        logger.error { mapOf(0 to 1, 2 to 3) }
        logger.warn(RuntimeException()) { 'c' }
        logger.error(RuntimeException()) { 0..100 }
        logger.warn(null) { assertNull(it) }
        logger.error(null) { assertNull(it) }
        logger.log(TRACE) { "message" }
        logger.log(ERROR, RuntimeException()) { 0..100 }
    }

    @Test fun `A logger for a custom name has the proper name`() {
        assert(Logger("name").name == "name")
        assert(Logger("name"::class).name == "kotlin.String")
    }

    @Test fun `A logger can be queried for its enabled state on a given level`() {
        assert(Logger("name").isLoggable(ERROR))
        assertFalse(Logger("name").isLoggable(TRACE))
    }

    @Test fun `ANSI testing`() {
        val l = Logger("name")
        val message = "$RED_BG$BRIGHT_WHITE${UNDERLINE}ANSI$RESET normal"
        val noAnsiMessage = l.stripAnsi(message, true)
        val ansiMessage = l.stripAnsi(message, false)
        assertEquals(message, ansiMessage)
        assertNotEquals(message, noAnsiMessage)
        assertContentEquals(noAnsiMessage?.toByteArray(), "ANSI normal".toByteArray())
    }

    @Test
    @DisabledInNativeImage
    fun `Invalid class name raises error`() {
        val kc = mockk<KClass<*>>()
        every { kc.qualifiedName } returns null
        val e = assertFailsWith<IllegalStateException> { Logger(kc) }
        assertEquals("Cannot get qualified name of type", e.message)
    }
}
