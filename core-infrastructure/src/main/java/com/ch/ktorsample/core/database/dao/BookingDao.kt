package com.ch.ktorsample.core.database.dao

import androidx.room.*
import com.ch.ktorsample.core.database.entities.BookingEntity
import com.ch.ktorsample.core.database.entities.BookingStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface BookingDao {
    
    @Query("SELECT * FROM bookings WHERE id = :bookingId")
    suspend fun getBookingById(bookingId: String): BookingEntity?
    
    @Query("SELECT * FROM bookings WHERE userId = :userId")
    fun getBookingsByUserId(userId: String): Flow<List<BookingEntity>>
    
    @Query("SELECT * FROM bookings WHERE userId = :userId AND status = :status")
    fun getBookingsByUserIdAndStatus(userId: String, status: BookingStatus): Flow<List<BookingEntity>>
    
    @Query("SELECT * FROM bookings WHERE movieId = :movieId")
    fun getBookingsByMovieId(movieId: String): Flow<List<BookingEntity>>
    
    @Query("SELECT * FROM bookings")
    fun getAllBookings(): Flow<List<BookingEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: BookingEntity)
    
    @Update
    suspend fun updateBooking(booking: BookingEntity)
    
    @Delete
    suspend fun deleteBooking(booking: BookingEntity)
    
    @Query("DELETE FROM bookings WHERE id = :bookingId")
    suspend fun deleteBookingById(bookingId: String)
    
    @Query("DELETE FROM bookings WHERE userId = :userId")
    suspend fun deleteBookingsByUserId(userId: String)
    
    @Query("DELETE FROM bookings")
    suspend fun deleteAllBookings()
}
