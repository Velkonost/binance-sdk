package velkonost.binance.sdk.extensions

import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

internal fun String.toMilliseconds(): Long {
    val epoch = ZonedDateTime.ofInstant(java.time.Instant.EPOCH, ZoneOffset.UTC)
    val date: ZonedDateTime = when {
        contains("ago") -> {
            val parts = split(" ")
            val amount = parts[0].toLong()
            val unit = parts[1]
            when (unit) {
                "day" -> ZonedDateTime.now(ZoneOffset.UTC).minusDays(amount)
                "hour" -> ZonedDateTime.now(ZoneOffset.UTC).minusHours(amount)
                "minute" -> ZonedDateTime.now(ZoneOffset.UTC).minusMinutes(amount)
                "second" -> ZonedDateTime.now(ZoneOffset.UTC).minusSeconds(amount)
                else -> throw IllegalArgumentException("Unknown time unit: $unit")
            }
        }
        contains("UTC") -> {
            ZonedDateTime.parse(replace("UTC", "Z"), DateTimeFormatter.ISO_ZONED_DATE_TIME)
        }
        else -> {
            try {
                ZonedDateTime.parse(this, DateTimeFormatter.ofPattern("MMMM dd, yyyy").withZone(ZoneOffset.UTC))
            } catch (e: Exception) {
                throw IllegalArgumentException("Unknown date format: $this")
            }
        }
    }

    return ChronoUnit.MILLIS.between(epoch, date)
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
        val unit = secondsPerUnit[last().toString()]
        assert(unit != null)
        number * unit!! * 1000
    } catch (e: Exception) {
        throw IllegalArgumentException("Unknown interval: $this")
    }
}