package com.hexagonkt.helpers

import com.hexagonkt.logging.Logger

/** Default logger for when you feel too lazy to declare one. */
val logger: Logger = Logger(Logger::class)

// THREADING ///////////////////////////////////////////////////////////////////////////////////////
/**
 * Execute a lambda until no exception is thrown or a number of times is reached.
 *
 * @param times Number of times to try to execute the callback. Must be greater than 0.
 * @param delay Milliseconds to wait to next execution if there was an error. Must be 0 or greater.
 * @param block Code to be executed.
 * @return Callback result if succeed.
 * @throws [MultipleException] if the callback didn't succeed in the given times.
 */
fun <T> retry(times: Int, delay: Long, block: () -> T): T {
    require(times > 0)
    require(delay >= 0)

    val exceptions = mutableListOf<Exception>()
    for (ii in 1 .. times) {
        try {
            return block()
        }
        catch (e: Exception) {
            exceptions.add(e)
            Thread.sleep(delay)
        }
    }

    throw MultipleException("Error retrying $times times ($delay ms)", exceptions)
}

// ERROR HANDLING //////////////////////////////////////////////////////////////////////////////////
/** Syntax sugar to throw errors. */
val fail: Nothing
    get() = error("Invalid state")

/**
 * Return the stack trace array of the frames that starts with the given prefix.
 */
fun Throwable.filterStackTrace(prefix: String): Array<out StackTraceElement> =
    if (prefix.isEmpty())
        this.stackTrace
    else
        this.stackTrace.filter { it.className.startsWith(prefix) }.toTypedArray()

/**
 * Return this throwable as a text.
 */
fun Throwable.toText(prefix: String = ""): String =
    "${this.javaClass.name}: ${this.message}" +
        this.filterStackTrace(prefix).joinToString(eol, eol) { "\tat $it" } +
        if (this.cause == null)
            ""
        else
            "${eol}Caused by: " + (this.cause as Throwable).toText(prefix)

// COLLECTIONS /////////////////////////////////////////////////////////////////////////////////////
fun <Z> Collection<Z>.ensureSize(count: IntRange): Collection<Z> = this.apply {
    if (size !in count) error("$size items while expecting only $count element")
}

@Suppress("UNCHECKED_CAST")
operator fun Map<*, *>.get(vararg keys: Any): Any? =
    if (keys.size > 1)
        keys
            .dropLast(1)
            .fold(this) { result, element ->
                val r = result as Map<Any, Any>
                when (val value = r[element]) {
                    is Map<*, *> -> value
                    is List<*> -> value.mapIndexed { ii, item -> ii to item }.toMap()
                    else -> emptyMap<Any, Any>()
                }
            }[keys.last()]
    else
        (this as Map<Any, Any>).getOrElse(keys.first()) { null }

@Suppress("UNCHECKED_CAST", "ReplaceGetOrSet")
fun <T : Any> Map<*, *>.requireKeys(vararg name: Any): T =
    this.get(*name) as? T ?: error("$name required key not found")

fun <K, V> Map<K, V>.require(name: K): V =
    this[name] ?: error("$name required key not found")

fun <K, V> Map<K, V?>.filterEmpty(): Map<K, V> =
    this.filterValues(::notEmpty).mapValues { (_, v) -> v ?: fail }

fun <V> List<V?>.filterEmpty(): List<V> =
    this.filter(::notEmpty).map { it ?: fail }

fun <V> notEmpty(it: V?): Boolean {
    return when (it) {
        null -> false
        is List<*> -> it.isNotEmpty()
        is Map<*, *> -> it.isNotEmpty()
        else -> true
    }
}
