package com.ch.ktorsample.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val genre: String,
    val duration: Int, // in minutes
    val rating: Double,
    val releaseDate: String,
    val posterUrl: String?,
    val trailerUrl: String?,
    val isPopular: Boolean = false,
    val isTrending: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long
)
