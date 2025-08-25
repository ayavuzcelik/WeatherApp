package com.adm.weatherapp.model

data class GetWeatherResponse(
    val base: String,
    val clouds: Clouds,
    val cod: Int,
    val coord: Coord,
    val dt: Int,
    val id: Int,
    val main: Main,
    val name: String,
    val sys: Sys,
    val timezone: Int,
    val visibility: Int,
    val weather: List<Weather>,
    val wind: Wind
) {
    override fun toString(): String {
        return buildString {
            appendLine("Şehir: $name")
            appendLine("Sıcaklık: ${main.temp}°C")
            appendLine("Hissedilen: ${main.feels_like}°C")
            appendLine("Nem: ${main.humidity}%")
            appendLine("Basınç: ${main.pressure} hPa")
            appendLine("Hava Durumu: ${weather.joinToString { it.main + " (${it.description})" }}")
            appendLine("Rüzgar: ${wind.speed} m/s, yön: ${wind.deg}°")
            appendLine("Bulutluluk: ${clouds.all}%")
            appendLine("Görüş Mesafesi: $visibility m")
        }
    }
}
