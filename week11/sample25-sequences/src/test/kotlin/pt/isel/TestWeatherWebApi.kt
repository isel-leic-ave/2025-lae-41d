package pt.isel

import org.junit.jupiter.api.Test
import pt.isel.weather.*
import java.time.LocalDate
import java.time.LocalDate.of
import java.util.function.Consumer
import kotlin.test.assertEquals

class TestWeatherWebApi {
    private val sidney2023List5Assert = listOf(13, 17, 18, 23, 25)

    @Test
    fun `test fetch weather for Lisbon`() {
        // ERROR API key of worldweatheronline has been disabled.
        fetchWeatherAndParse("Lisbon", of(2021, 1, 17))
            .forEach { println(it) }
    }

//    @Test
//    fun `test fetch and save weather for Lisbon`() {
//        fetchAndSave("sidney", 2023)
//    }

    @Test
    fun `test load file with weather for Barcelona`() {
        loadMonthFileWeatherAndParse("Barcelona", of(2023, 1, 1))
            .forEach { println(it) }
    }

    @Test
    fun `select top 5 temperatures in Sidney with pipeline`() {
        val pastWeather: List<Weather> = loadMonthFileWeatherAndParse("Sidney", of(2023, 4, 1))
        var count = 0
        val top5temps = pastWeather
            .filter { count++; it.isSunny }
            .map { count++; it.celsius }
            .take(5)
        val actual = top5temps.iterator()
        sidney2023List5Assert
            .forEach { assertEquals(it, actual.next()) }
        assertEquals(35, count)
    }

    @Test
    fun `select top 5 temperatures in Sidney with imperative`() {
        val pastWeather = loadMonthFileWeatherAndParse("Sidney", of(2023, 4, 1))
        val top5temps = mutableListOf<Int>()
        for (w in pastWeather) {
            if (w.isSunny) {               // <=> filter
                top5temps.add(w.celsius)   // <=> map
                if (top5temps.size >= 5) { // <=> take
                    break
                }
            }
        }
        val actual = top5temps.iterator()
        sidney2023List5Assert
            .forEach { assertEquals(it, actual.next()) }
    }

    @Test
    fun `select top 5 temperatures in Sidney with pipeline lazy`() {
        val pastWeather = loadMonthFileWeatherAndParse("Sidney", of(2023, 4, 1))
        var count = 0
        val top5temps = pastWeather
            .asSequence()
            .filter { count++; it.isSunny }
            .map { count++; it.celsius }
            .take(5)
        val actual = top5temps.iterator()
        sidney2023List5Assert
            .forEach { assertEquals(it, actual.next()) }
        assertEquals(18, count)
    }

    @Test
    fun `select top 5 temperatures in Sidney with pipeline lazy in Java`() {
        val pastWeather = loadMonthFileWeatherAndParse("Sidney", of(2023, 4, 1))
        var count = 0
        val top5temps = pastWeather
            .stream()
            .filter { count++; it.isSunny }
            .map { count++; it.celsius }
            .limit(5)
        val actual = top5temps.iterator()
        sidney2023List5Assert
            .forEach { assertEquals(it, actual.next()) }
        assertEquals(18, count)
    }

    @Test
    fun `check interleave explicit temperatures in celsius`() {
        val sidneyJan5days = loadMonthFileWeatherAndParse("Sidney", of(2024, 7, 1)).take(5)
        val sidneyFeb5days = loadMonthFileWeatherAndParse("Sidney", of(2024, 8, 1)).take(5)
        sidneyJan5days
            .asSequence()
            .map(Weather::celsius)
            .interleaveExt(sidneyFeb5days.asSequence().map(Weather::celsius))
            //.forEach { print("$it ") }
            .forEachIndexed { index, item ->
                if (index % 2 == 0) assertEquals(sidneyJan5days[index / 2].celsius, item)
                else assertEquals(sidneyFeb5days[index / 2].celsius, item)
            }
    }

    @Test
    fun `iterate for 5 first weather description in tryAdvance with Spliterator`() {
        val pastWeather = loadMonthFileWeatherAndParse("Sidney", of(2023, 4, 1)).take(5)
        val iter = pastWeather.stream().spliterator()
        while (iter.tryAdvance { w ->
                println("${w.date}: ${w.weatherDesc}")
            }){ /*while block here is empty*/ }
    }

    @Test
    fun `select top 5 temperatures in Sidney with imperative in tryAdvance mode with Spliterator`() {
        val pastWeather = loadMonthFileWeatherAndParse("Sidney", of(2023, 4, 1))
        val top5temps = mutableListOf<Int>()
        val iter = pastWeather.stream().spliterator()
        while (iter.tryAdvance { w ->
                if (w.isSunny) {               // <=> filter
                    top5temps.add(w.celsius)   // <=> map
                }
            }) {
            if (top5temps.size >= 5) { // <=> take
                break
            }
        }
        val actual = top5temps.iterator()
        sidney2023List5Assert
            .forEach { assertEquals(it, actual.next()) }
    }

    @Test
    fun `check interleave temperatures in celsius for Java streams`() {
        val sidneyJan5days = loadMonthFileWeatherAndParse("Sidney", of(2024, 7, 1)).take(5)//.toList().also{println(it.map(Weather::celsius))}
        val sidneyFeb5days = loadMonthFileWeatherAndParse("Sidney", of(2024, 8, 1)).take(5)//.toList().also{println(it.map(Weather::celsius))}
        var index = 0
        sidneyJan5days
            .stream()
            .map(Weather::celsius)
            .interleave(sidneyFeb5days.stream().map(Weather::celsius))
            .forEach { item -> // Note: there is no forEachIndex for a Stream
                //println(item)
                if (index % 2 == 0) assertEquals(sidneyJan5days[index / 2].celsius, item)
                else assertEquals(sidneyFeb5days[index / 2].celsius, item)
                index++
            }
    }

    @Test
    fun `check interleave generator temperatures in celsius`() {
        val sidneyJan5days = loadMonthFileWeatherAndParse("Sidney", of(2024, 7, 1)).take(5)
        val sidneyFeb5days = loadMonthFileWeatherAndParse("Sidney", of(2024, 8, 1)).take(5)
        sidneyJan5days
            .asSequence()
            .map(Weather::celsius)
            .interleaveGen(sidneyFeb5days.asSequence().map(Weather::celsius))
            .forEachIndexed { index, item ->
                println("$index: $item")
                if (index % 2 == 0) assertEquals(sidneyJan5days[index / 2].celsius, item)
                else assertEquals(sidneyFeb5days[index / 2].celsius, item)
            }
    }

    @Test
    fun `check rainy days in Lisbon`() {
        generateSequence(LocalDate.of(2024, 1, 1)) { // starting on 2024-1-1
            it.plusMonths(1) // infinity
        }
            .take(5)                   // 5 months
            .flatMap { loadMonthFileWeatherAndParse("Lisbon", it) }  // In Lisbon
            .filter{ it.isRainy } // Rainy days
            .map(Weather::weatherDesc) // Select the description
            .distinct()                // unique values
            .forEach { println(it) }
    }

    @Test
    fun `test load files with weather for Sidney since 2023`() {
        generateSequence(of(2023, 1, 1)) { it.plusMonths(1) }
            .take(24) // 24 months
            .flatMap { loadMonthFileWeatherAndParse("Sidney", it) }
            .forEach { println(it) }
    }

    @Test
    fun `check interleave temperatures in celsius with position`() {
        val sidneyJan5days = loadMonthFileWeatherAndParse("Sidney", of(2023, 4, 1))
        sidneyJan5days
            .asSequence()
            .filter(Weather::isSunny)
            .map(Weather::celsius)
            .interleave(generateSequence(1) { it + 1 }.map { " ($it) " })
            .take(10)
            .forEach { print(it) }
    }

}