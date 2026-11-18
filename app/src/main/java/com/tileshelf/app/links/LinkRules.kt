package com.tileshelf.app.links

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

/**
 * Query parameters to strip, in the shape of Firefox's strip-on-share lists:
 * names are compared lowercased, per exact host or per site (a host and its subdomains).
 */
class StripRules(
    val global: Set<String>,
    val byHost: Map<String, Set<String>>,
    val bySite: Map<String, Set<String>>,
) {
    fun stripsParam(host: String, name: String): Boolean {
        if (name in global) return true
        if (byHost[host]?.contains(name) == true) return true
        var site = host
        while (true) {
            if (bySite[site]?.contains(name) == true) return true
            val dot = site.indexOf('.')
            if (dot < 0) return false
            site = site.substring(dot + 1)
        }
    }
}

/** A ClearURLs provider's redirections: the first capture group is the real link. */
class RedirectRule(
    val urlPattern: Regex,
    val exceptions: List<Regex>,
    val redirections: List<Regex>,
)

class LinkRules(val strip: StripRules, val redirects: List<RedirectRule>) {
    companion object {
        private const val DIR = "link-rules"
        private const val FIREFOX = "firefox-strip-on-share.json"
        private const val FIREFOX_LGPL = "firefox-strip-on-share-lgpl.json"
        private const val CLEARURLS = "clearurls-redirects.json"

        @Volatile
        private var loaded: LinkRules? = null

        fun get(context: Context): LinkRules = loaded ?: synchronized(this) {
            loaded ?: run {
                val assets = context.applicationContext.assets
                fun read(name: String) = assets.open("$DIR/$name").bufferedReader().use { it.readText() }
                parse(listOf(read(FIREFOX), read(FIREFOX_LGPL)), read(CLEARURLS)).also { loaded = it }
            }
        }

        fun parse(stripLists: List<String>, redirectsJson: String): LinkRules =
            LinkRules(parseStrip(stripLists), parseRedirects(redirectsJson))

        private fun parseStrip(lists: List<String>): StripRules {
            val global = mutableSetOf<String>()
            val byHost = mutableMapOf<String, MutableSet<String>>()
            val bySite = mutableMapOf<String, MutableSet<String>>()
            for (list in lists) {
                val root = JSONObject(list)
                for (key in root.keys()) {
                    val rule = root.getJSONObject(key)
                    val params = rule.optJSONArray("queryParams").strings().map { it.lowercase() }
                    if (rule.optBoolean("isGlobal")) global += params
                    rule.optJSONArray("hosts").strings().forEach {
                        byHost.getOrPut(it.lowercase()) { mutableSetOf() } += params
                    }
                    rule.optJSONArray("schemelessSites").strings().forEach {
                        bySite.getOrPut(it.lowercase()) { mutableSetOf() } += params
                    }
                }
            }
            return StripRules(global, byHost, bySite)
        }

        private fun parseRedirects(json: String): List<RedirectRule> {
            val providers = JSONArray(json)
            return (0 until providers.length()).map { index ->
                val provider = providers.getJSONObject(index)
                RedirectRule(
                    urlPattern = provider.getString("urlPattern").toRegex(RegexOption.IGNORE_CASE),
                    exceptions = provider.optJSONArray("exceptions").strings()
                        .map { it.toRegex(RegexOption.IGNORE_CASE) },
                    redirections = provider.optJSONArray("redirections").strings()
                        .map { it.toRegex(RegexOption.IGNORE_CASE) },
                )
            }
        }

        private fun JSONArray?.strings(): List<String> =
            if (this == null) emptyList() else (0 until length()).map { getString(it) }
    }
}
