package pt.isel.weather

import java.io.File
import java.lang.String.format
import java.net.URI
import java.time.LocalDate
import java.time.Month
import java.util.logging.Logger
import kotlin.collections.filter
import kotlin.collections.filterIndexed


private val currentDir = System.getProperty("user.dir")
private const val HOST = "http://api.worldweatheronline.com/premium/v1/"
private const val PATH_PAST_WEATHER = "past-weather.ashx?q=%s&date=%s&enddate=%s&tp=24&format=csv&key=%s"
private const val PATH_SEARCH = "search.ashx?query=%s&format=csv&key=%s"
private val WEATHER_KEY = System.getenv("API_WORLD_WEATHER_KEY")
/**
 * past-weather API of World Weather Online is limited to 30 items per page.
 * Thus, we fetch only 1 month from the given date.
 *
 * E.g. http://api.worldweatheronline.com/premium/v1/past-weather.ashx?q=lisbon&date=2024-01-01&enddate=2024-04-30&tp=24&format=csv&key=$WEATHER_KEY
 */
fun fetchWeather(location: String, since: LocalDate): String {
    Logger.getLogger("pt.isel").info("Fetching for $location in $since")
    val to = since.plusMonths(1)
    val path = HOST + format(PATH_PAST_WEATHER, location, since, to, WEATHER_KEY)
    //URI(path).toURL().readText().lines().forEach { println("---> $it") }
    return URI(path).toURL().readText()
}

/**
 * Read locally from a file
 */
fun loadMonthFileWeatherAndParse(location: String, since: LocalDate): List<Weather> {
    val loc = location.lowercase().replace(" ", "_")
    val filename = "$currentDir/src/test/resources/past-weather-$loc-$since.csv"
    return File(filename)
        .readLines()
        .parseWeatherFile(since.month)
        .map { it.fromCsvToWeather() }
}

fun List<String>.parseWeatherFile(month: Month): List<String> {
    return this
        .filter { !it.startsWith('#') } // Filter comments
        .drop(1)                        // Skip line: Not available
        .filterIndexed {                // Filter hourly info
                index, _ ->  index % 2 != 0
        }
        .filter { it.fromCsvToWeather().date.month <= month }
}


fun fetchWeatherAndParse(location: String, since: LocalDate): List<Weather> {
    return fetchWeather(location, since)
        .lines()
        .parseWeatherFile(since.month)
        .map { it.fromCsvToWeather() }
}

/**
 * E.g. use with: fetchAndSave("New+York", 2021)
 */
fun fetchAndSave(location: String, sinceYear: Int) {
    generateSequence(LocalDate.of(sinceYear,1,1)) {
        it.plusMonths(1)
    }
        .takeWhile { date -> date < LocalDate.now() }
        .map { date -> Pair(date, fetchWeather(location, date)) }
        .forEach {(date, csv) ->
            File("$currentDir/src/test/resources/past-weather-$location-$date.csv")
                .writeText(csv)
        }
}