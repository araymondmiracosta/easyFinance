package net.araymond.application

import java.io.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class Transaction(
    var category: String,
    var description: String,
    var amount: Double,
    var date: LocalDate,
    var time: LocalTime,
    var account: String
) : Serializable {
    var localDateTime = LocalDateTime.of(date, time)
}