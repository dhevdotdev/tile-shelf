package com.tileshelf.app.links

import java.net.URLDecoder
import java.net.URLEncoder

data class CleanedText(val text: String, val removed: Int, val unwrapped: Int) {
    val changed: Boolean get() = removed > 0 || unwrapped > 0
}

/**
 * Removes tracking parameters from every http(s) link in a piece of text and unwraps
 * known redirect links. Only the tracking parts change; the rest of the text,
 * the remaining parameters and the fragment are kept byte for byte.
 */
class LinkCleaner(private val rules: LinkRules) {

    fun containsLink(text: String): Boolean = LINK.containsMatchIn(text)

    fun clean(text: String): CleanedText {
        var removed = 0
        var unwrapped = 0
        val result = LINK.replace(text) { match ->
            val (link, tail) = splitTrailing(match.value)
            var current = link
            var hops = 0
            while (hops < MAX_REDIRECTS) {
                current = unwrap(current) ?: break
                hops++
            }
            unwrapped += hops
            val stripped = strip(current, nested = false)
            removed += stripped.removed
            stripped.link + tail
        }
        return CleanedText(result, removed, unwrapped)
    }

    private fun unwrap(link: String): String? {
        for (rule in rules.redirects) {
            if (!rule.urlPattern.containsMatchIn(link)) continue
            if (rule.exceptions.any { it.containsMatchIn(link) }) continue
            for (redirect in rule.redirections) {
                val captured = redirect.find(link)?.groupValues?.getOrNull(1) ?: continue
                val target = asLink(decodeFully(captured)) ?: continue
                if (target != link) return target
            }
        }
        return null
    }

    private data class Stripped(val link: String, val removed: Int)

    private fun strip(link: String, nested: Boolean): Stripped {
        val hashAt = link.indexOf('#')
        val beforeHash = if (hashAt >= 0) link.substring(0, hashAt) else link
        val fragment = if (hashAt >= 0) link.substring(hashAt) else ""
        val queryAt = beforeHash.indexOf('?')
        if (queryAt < 0) return Stripped(link, 0)
        val base = beforeHash.substring(0, queryAt)
        val host = hostOf(base) ?: return Stripped(link, 0)

        var removed = 0
        val kept = mutableListOf<String>()
        for (pair in beforeHash.substring(queryAt + 1).split('&')) {
            if (pair.isEmpty()) continue
            val name = pair.substringBefore('=')
            if (rules.strip.stripsParam(host, decode(name).lowercase())) {
                removed++
                continue
            }
            if (!nested && '=' in pair) {
                val inner = asLink(decode(pair.substringAfter('=')), allowBare = false)
                if (inner != null) {
                    val cleaned = strip(inner, nested = true)
                    if (cleaned.removed > 0) {
                        removed += cleaned.removed
                        kept += name + "=" + URLEncoder.encode(cleaned.link, "UTF-8")
                        continue
                    }
                }
            }
            kept += pair
        }
        if (removed == 0) return Stripped(link, 0)
        val query = if (kept.isEmpty()) "" else "?" + kept.joinToString("&")
        return Stripped(base + query + fragment, removed)
    }

    companion object {
        private const val MAX_REDIRECTS = 5
        private val LINK = Regex("""https?://[^\s<>"'`]+""", RegexOption.IGNORE_CASE)
        private val SCHEME = Regex("^https?://", RegexOption.IGNORE_CASE)
        private val BARE_HOST = Regex("""^[a-z0-9-]+(\.[a-z0-9-]+)*\.[a-z]{2,}(?:[/:?#]|$)""", RegexOption.IGNORE_CASE)
        private const val TRAILING = ".,;:!?"

        private fun splitTrailing(match: String): Pair<String, String> {
            var end = match.length
            while (end > 0) {
                val last = match[end - 1]
                val unbalanced = when (last) {
                    ')' -> match.substring(0, end).count { it == '(' } < match.substring(0, end).count { it == ')' }
                    ']' -> match.substring(0, end).count { it == '[' } < match.substring(0, end).count { it == ']' }
                    else -> false
                }
                if (last in TRAILING || unbalanced) end-- else break
            }
            return match.substring(0, end) to match.substring(end)
        }

        private fun hostOf(base: String): String? {
            val afterScheme = base.substringAfter("://", missingDelimiterValue = "")
            if (afterScheme.isEmpty()) return null
            val authority = afterScheme.substringBefore('/')
            val host = authority.substringAfterLast('@').substringBefore(':').lowercase()
            return host.ifEmpty { null }
        }

        /** An http(s) link, or a bare host such as Google AMP's /amp/s/ target given https. */
        private fun asLink(candidate: String, allowBare: Boolean = true): String? {
            val value = candidate.trim()
            if (value.any { it.isWhitespace() }) return null
            if (SCHEME.containsMatchIn(value)) return value
            return if (allowBare && BARE_HOST.containsMatchIn(value)) "https://$value" else null
        }

        /** Percent-decoding that leaves '+' alone, like decodeURIComponent. */
        private fun decode(value: String): String =
            if ('%' !in value) value
            else try {
                URLDecoder.decode(value.replace("+", "%2B"), "UTF-8")
            } catch (_: IllegalArgumentException) {
                value
            }

        private fun decodeFully(value: String): String {
            var current = value
            repeat(MAX_REDIRECTS) {
                val next = decode(current)
                if (next == current) return current
                current = next
            }
            return current
        }
    }
}
