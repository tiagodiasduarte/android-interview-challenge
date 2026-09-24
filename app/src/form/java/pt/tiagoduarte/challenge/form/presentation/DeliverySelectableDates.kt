package pt.tiagoduarte.challenge.form.presentation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import pt.tiagoduarte.challenge.form.validation.FormValidator
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
class DeliverySelectableDates(private val today: LocalDate) : SelectableDates {

    override fun isSelectableDate(utcTimeMillis: Long): Boolean =
        FormValidator.validateDeliveryDate(utcTimeMillis.toLocalDate(), today) == null

    override fun isSelectableYear(year: Int): Boolean = year <= today.year
}

// The date picker works in UTC milliseconds at the start of the day
fun Long.toLocalDate(): LocalDate = Instant.ofEpochMilli(this).atZone(ZoneOffset.UTC).toLocalDate()

fun LocalDate.toUtcMillis(): Long = atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
