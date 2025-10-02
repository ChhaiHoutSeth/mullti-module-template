package com.ch.ktorsample.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val movieId: String,
    val showTime: String,
    val seatNumbers: List<String>,
    val totalPrice: Double,
    val status: BookingStatus,
    val bookingDate: Long,
    val createdAt: Long,
    val updatedAt: Long
)

enum class BookingStatus {
    PENDING,
    CONFIRMED,
    CANCELLED,
    COMPLETED
}
