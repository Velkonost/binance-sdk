package velkonost.binance.sdk.extensions

import kotlinx.datetime.*
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import kotlin.time.Duration.Companion.milliseconds


internal fun String.toMilliseconds(): Long {
    val epoch = Instant.fromEpochMilliseconds(0)
    val date: Instant = when {
        contains("ago") -> {
            val parts = split(" ")
            val amount = parts[0].toLong()
            val unit = parts[1]
            Clock.System.now().minus(
                when (unit) {
                    "day" -> amount * 24 * 3600 * 1000
                    "hour" -> amount * 3600 * 1000
                    "minute" -> amount * 60 * 1000
                    "second" -> amount * 1000
                    else -> throw IllegalArgumentException("Unknown time unit: $unit")
                }.milliseconds
            )
        }

        contains("UTC") -> {
            Instant.parse(replace("UTC", "Z"))
        }

        else -> {
            try {
                val format =
                    LocalDate.Format {
                        monthName(MonthNames.ENGLISH_FULL)
                        char(' ')
                        dayOfMonth()
                        chars(", ")
                        year()
                    }
                LocalDate.parse(this, format)
                    .atStartOfDayIn(TimeZone.UTC)
            } catch (e: Exception) {
                throw IllegalArgumentException("Unknown date format: $this", e)
            }
        }
    }

    return (date - epoch).inWholeMilliseconds
}

internal fun String.convertIntervalToMills(): Int {
    val secondsPerUnit = mapOf(
        "m" to 60,
        "h" to 60 * 60,
        "d" to 24 * 60 * 60,
        "w" to 7 * 24 * 60 * 60
    )
    return try {
        val number = dropLast(1).toInt()
        val unit = secondsPerUnit.getOrElse(last().toString()) {
            throw IllegalArgumentException("Unknown interval: $this")
        }
        number * unit * 1000
    } catch (e: Exception) {
        throw IllegalArgumentException("Unknown interval: $this")
    }
}


fun convertToMillis(dateTimeStr: String): Long {
    val parts = dateTimeStr.split(" ", "-", ":")
    require(parts.size == 6) { "Incorrect format. Expected: 'YYYY-MM-DD HH:MM:SS'" }

    val year = parts[0].toInt()
    val month = parts[1].toInt()
    val day = parts[2].toInt()
    val hour = parts[3].toInt()
    val minute = parts[4].toInt()
    val second = parts[5].toInt()

    val dateTime = LocalDateTime(year, month, day, hour, minute, second)

    return dateTime.toInstant(TimeZone.UTC).toEpochMilliseconds()
}