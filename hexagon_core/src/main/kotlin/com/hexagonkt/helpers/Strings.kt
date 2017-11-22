package com.hexagonkt.helpers

import java.io.ByteArrayInputStream
import java.io.InputStream
import java.lang.System.getProperty
import java.text.Normalizer.Form.NFD
import java.text.Normalizer.normalize

/** Variable prefix for string filtering. It starts with '#' because of Kotlin's syntax. */
private const val VARIABLE_PREFIX = "#{"
/** Variable sufix for string filtering. */
private const val VARIABLE_SUFFIX = "}"

/** Start of ANSI sequence. */
private const val ANSI_PREFIX = "\u001B["
/** End of ANSI sequence. */
private const val ANSI_END = "m"

/** Separator for commands inside a single ANSI sequence. */
private const val ANSI_SEPARATOR = ";"
/** ANSI command to reset all attributes. */
private const val ANSI_RESET = "0"

/** ANSI foreground color base. */
const val FOREGROUND = 30
/** ANSI background color base. */
const val BACKGROUND = 40

/** ANSI modifier to switch and effect (add to enable substract todisable). */
const val SWITCH_EFFECT = 20

/** Runtime specific end of line. */
val eol: String = getProperty("line.separator")

/**
 * Filters the target string substituting each key by its value. The keys format is:
 * `#{key}` and all occurrences are replaced by the supplied value.
 *
 * If a variable does not have a parameter, it is left as it is.
 *
 * @param parameters The map with the list of key/value tuples.
 * @return The filtered text or the same string if no values are passed or found in the text.
 * @sample com.hexagonkt.helpers.StringsTest.filterVarsExample
 */
fun String.filterVars (parameters: Map<*, *>): String =
    parameters.entries
        .filter { it.key.toString().isNotEmpty() }
        .fold(this) { result, pair ->
            val key = pair.key.toString()
            val value = pair.value.toString()
            result.replace ("$VARIABLE_PREFIX$key$VARIABLE_SUFFIX", value)
        }

fun String.filterVars (vararg parameters: Pair<*, *>) = this.filterVars(mapOf (*parameters))

fun String.filter (
    prefix: String, suffix: String, vararg parameters: Pair<String, String>): String =
        parameters.fold(this) { result, (first, second) ->
            result.replace (prefix + first + suffix, second)
        }

fun Regex.findGroups (str: String): List<MatchGroup> =
    (this.find (str)?.groups ?: listOf<MatchGroup> ())
        .map { it ?: throw IllegalArgumentException () }
        .drop(1)

/**
 * Transforms the target string from snake case to camel case.
 */
fun String.snakeToCamel (): String =
    this.split ("_")
        .filter(String::isNotEmpty)
        .joinToString("", transform = String::capitalize)
        .decapitalize ()

/**
 * Transforms the target string from camel case to snake case.
 */
fun String.camelToSnake (): String =
    this.split ("(?=\\p{Upper}\\p{Lower})".toRegex())
        .filter(String::isNotEmpty)
        .joinToString ("_", transform = String::toLowerCase)
        .decapitalize ()

/**
 * Formats the string as a banner with a delimiter above and below text. The character used to
 * render the delimiter is defined.
 *
 * @param bannerDelimiter Delimiter char for banners.
 */
fun String.banner (bannerDelimiter: String = "*"): String {
    val separator = bannerDelimiter.repeat (this.lines().map { it.length }.max() ?: 0)
    return "$separator$eol$this$eol$separator"
}

fun String.stripAccents(): String = normalize(this, NFD).replace("\\p{M}".toRegex(), "")

fun String.toStream(): InputStream = ByteArrayInputStream(this.toByteArray())

fun utf8(vararg bytes: Int): String = String(bytes.map(Int::toByte).toByteArray())

private fun ansiCode (fg: AnsiColor?, bg: AnsiColor?, vararg fxs: AnsiEffect): String {
    fun fgString (color: AnsiColor?) = (color?.fg ?: "").toString()
    fun bgString (color: AnsiColor?) = (color?.bg ?: "").toString()

    val colors = listOf (fgString(fg), bgString(bg))
    val elements = colors + fxs.map { it.code.toString() }
    val body = elements.filter(String::isNotEmpty).joinToString (ANSI_SEPARATOR)

    return ANSI_PREFIX + (if (body.isEmpty()) ANSI_RESET else body) + ANSI_END
}

/**
 * Creates an ANSI sequence composed by a list of commands (colors, effects, etc.).
 *
 * A call with no effect neither color generates an ANSI reset.
 *
 * @param fg Foreground color.
 * @param bg Background color.
 * @param fxs List of affects
 * @return The ANSI sequence
 */
fun ansi (fg: AnsiColor, bg: AnsiColor, vararg fxs: AnsiEffect): String = ansiCode (fg, bg, *fxs)

fun ansi (fg: AnsiColor, vararg fxs: AnsiEffect): String = ansiCode (fg, null, *fxs)

fun ansi (vararg fxs: AnsiEffect): String = ansiCode (null, null, *fxs)
