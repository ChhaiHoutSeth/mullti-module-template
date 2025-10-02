package com.ch.ktorsample.core.database.dao

import androidx.room.*
import com.ch.ktorsample.core.database.entities.MovieEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    
    @Query("SELECT * FROM movies WHERE id = :movieId")
    suspend fun getMovieById(movieId: String): MovieEntity?
    
    @Query("SELECT * FROM movies")
    fun getAllMovies(): Flow<List<MovieEntity>>
    
    @Query("SELECT * FROM movies WHERE isPopular = 1")
    fun getPopularMovies(): Flow<List<MovieEntity>>
    
    @Query("SELECT * FROM movies WHERE isTrending = 1")
    fun getTrendingMovies(): Flow<List<MovieEntity>>
    
    @Query("SELECT * FROM movies WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    fun searchMovies(query: String): Flow<List<MovieEntity>>
    
    @Query("SELECT * FROM movies WHERE genre = :genre")
    fun getMoviesByGenre(genre: String): Flow<List<MovieEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: MovieEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<MovieEntity>)
    
    @Update
    suspend fun updateMovie(movie: MovieEntity)
    
    @Delete
    suspend fun deleteMovie(movie: MovieEntity)
    
    @Query("DELETE FROM movies WHERE id = :movieId")
    suspend fun deleteMovieById(movieId: String)
    
    @Query("DELETE FROM movies")
    suspend fun deleteAllMovies()
}
