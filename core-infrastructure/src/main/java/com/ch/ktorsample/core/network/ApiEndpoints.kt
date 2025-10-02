package com.ch.ktorsample.core.network

/**
 * Centralized API endpoints configuration
 */
object ApiEndpoints {
    const val BASE_URL = "https://api.example.com/v1"
    
    // Authentication endpoints
    object Auth {
        const val LOGIN = "/auth/login"
        const val REGISTER = "/auth/register"
        const val REFRESH = "/auth/refresh"
        const val LOGOUT = "/auth/logout"
        const val BIOMETRIC_LOGIN = "/auth/biometric"
    }
    
    // User account endpoints
    object Account {
        const val PROFILE = "/account/profile"
        const val UPDATE_PROFILE = "/account/profile"
        const val DELETE_ACCOUNT = "/account/delete"
    }
    
    // Movie endpoints
    object Movie {
        const val LIST = "/movies"
        const val DETAIL = "/movies/{id}"
        const val SEARCH = "/movies/search"
        const val POPULAR = "/movies/popular"
        const val TRENDING = "/movies/trending"
    }
    
    // Booking endpoints
    object Booking {
        const val CREATE = "/bookings"
        const val LIST = "/bookings"
        const val DETAIL = "/bookings/{id}"
        const val CANCEL = "/bookings/{id}/cancel"
        const val HISTORY = "/bookings/history"
    }
}
