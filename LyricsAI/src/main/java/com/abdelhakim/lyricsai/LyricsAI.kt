/*
 * LyricsAI - A library for retrieving song lyrics using web scraping.
 * Author: Abdelhakim Khaouiti
 * GitHub: https://github.com/khaouitiabdelhakim
 * Last Modified: 2023-10-22
 *
 * This library is provided for Android developers to retrieve song lyrics through web scraping.
 * You are allowed to use this library in your Android projects, but please respect that this library belongs to its author.
 */


package com.abdelhakim.lyricsai


import android.os.Build
import android.text.Html
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder


object LyricsAI {

    private const val MIN_LENGTH = 200

    suspend fun findLyricsBySongTitle(title : String): String {
        return find(title)
    }

    suspend fun findLyricsBySongTitleAndArtist(title : String, artist : String): String {
        return find("$title $artist")
    }


    private suspend fun find(info: String): String = withContext(Dispatchers.IO) {
        val builder = StringBuilder()

        val myUrl = "https://www.google.com/search?q=${("$info lyrics").encodeParam()}"

        try {
            val response = getHtmlContent(myUrl)
            val document = Jsoup.parse(response)
            val lyricsElement = document.selectFirst("div[jsname=WbKHeb]")

            if (lyricsElement != null) {
                return@withContext if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Html.fromHtml(lyricsElement.html(), Html.FROM_HTML_MODE_LEGACY).toString()
                } else {
                    @Suppress("DEPRECATION")
                    Html.fromHtml(lyricsElement.html()).toString()
                }
            }

            for (link in links(info)) {
                try {
                    val response = getHtmlContent(link)
                    val document: Document = Jsoup.parse(response)

                    var divWithMostBr: Element? = null
                    var maxBrCount = 0

                    val divElements = document.select("*:not(:has(div))")

                    for (divElement in divElements) {
                        val brCount = divElement.select("br").size
                        if (brCount >= maxBrCount) {
                            maxBrCount = brCount
                            divWithMostBr = divElement
                        }
                    }

                    if (divWithMostBr?.text().toString().length > 600) {

                        Log.d("lyricos",link)
                        val lyricContent = divWithMostBr?.html().toString()
                        return@withContext if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            removeLinesWithBrackets(Html.fromHtml(lyricContent, Html.FROM_HTML_MODE_LEGACY).toString())
                        } else {
                            @Suppress("DEPRECATION")
                            removeLinesWithBrackets(Html.fromHtml(lyricContent).toString())
                        }
                    }
                } catch (_: IOException) {
                    // Handle the exception if needed
                }
            }
        } catch (e: IOException) {
            // Handle the exception if needed
        }

        return@withContext builder.toString()
    }
    private fun renderHtml(htmlContent: String): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(htmlContent, Html.FROM_HTML_MODE_LEGACY).toString()
        } else {
            @Suppress("DEPRECATION")
            Html.fromHtml(htmlContent).toString()
        }
    }


    private fun removeLinesWithBrackets(inputText: String): String {
        val lines = inputText.lines()
        val filteredLines = lines.filter { !it.contains("[") || !it.contains("]") }
        return filteredLines.joinToString("\n")
    }



    private suspend fun findLyrics(url: String): String? = withContext(Dispatchers.IO) {
        val response = getHtmlContent(url)
        val elements = Jsoup.parse(response).body().select("*")

        var maxBr = 0
        var result: Element? = null

        elements.forEach { element ->
            val brElements = element.children().count { it.tag().name.equals("br") }

            if (brElements > maxBr) {
                maxBr = brElements
                result = element
            }
        }

        result?.let(::getText)
    }


    private fun getText(element: Element): String {
        val sb = StringBuilder()

        for (child in element.childNodes()) {
            if (child is TextNode)
                sb.append(child.text())

            if (child is Element) {
                if (child.tag().name.equals("br", ignoreCase = true))
                    sb.append("\n")

                sb.append(getText(child))
            }
        }

        return sb.toString()
    }

    private suspend fun links(info: String): ArrayList<String> = withContext(Dispatchers.IO) {

        val links = ArrayList<String>()

        val encodedInfo = "$info lyrics".encodeParam()

        val myUrl = "https://www.google.com/search?q=$encodedInfo&num=15"

        val response = getHtmlContent(myUrl)
        val linksToSong = Jsoup.parse(response).select("div[class=yuRUbf]")

        for (link in linksToSong) {
            val href = link.select("a").first()!!.attr("href")
            if (isWebpage(href)) links.add(href)

        }

        return@withContext removeDuplicates(links)
    }

    fun removeDuplicates(list: ArrayList<String>): ArrayList<String> {
        val distinctList = ArrayList<String>(HashSet(list))
        return distinctList
    }

    fun isWebpage(url: String): Boolean {
        try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = "HEAD"
            connection.connect()

            val contentType = connection.contentType
            return contentType?.startsWith("text/html") == true
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }



    private suspend fun getHtmlContent(url: String): String = withContext(Dispatchers.IO) {
        val httpURLConnection = URL(url).openConnection() as HttpURLConnection

        httpURLConnection.setRequestProperty(
            "User-Agent",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.66 Safari/537.36"
        )

        httpURLConnection.inputStream.bufferedReader().readText()
    }

    private fun String.encodeParam(): String = URLEncoder.encode(this, "utf-8")


}