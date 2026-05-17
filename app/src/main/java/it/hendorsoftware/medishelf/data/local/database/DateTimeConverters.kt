package it.hendorsoftware.medishelf.data.local.database

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDate

/**
 * Converter Room per i tipi temporali usati dal modello persistente.
 *
 * LocalDate viene salvato in formato ISO-8601, mentre Instant viene salvato come
 * epoch millis per mantenere un ordinamento semplice nelle query.
 */
class DateTimeConverters {

    /**
     * Converte una data locale in stringa ISO-8601 nullable.
     *
     * @param value data locale da persistere.
     * @return rappresentazione testuale ISO oppure null.
     */
    @TypeConverter
    fun localDateToString(value: LocalDate?): String? = value?.toString()

    /**
     * Ricostruisce una data locale da stringa ISO-8601 nullable.
     *
     * @param value valore letto dal database.
     * @return data locale corrispondente oppure null.
     */
    @TypeConverter
    fun stringToLocalDate(value: String?): LocalDate? = value?.let(LocalDate::parse)

    /**
     * Converte un istante in epoch millis nullable.
     *
     * @param value istante da persistere.
     * @return millisecondi da epoch oppure null.
     */
    @TypeConverter
    fun instantToEpochMillis(value: Instant?): Long? = value?.toEpochMilli()

    /**
     * Ricostruisce un istante da epoch millis nullable.
     *
     * @param value valore numerico letto dal database.
     * @return istante corrispondente oppure null.
     */
    @TypeConverter
    fun epochMillisToInstant(value: Long?): Instant? = value?.let(Instant::ofEpochMilli)
}
