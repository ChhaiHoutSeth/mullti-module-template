package com.ch.ktorsample.core.network

import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

/**
 * Centralized API client configuration for the application
 */
object ApiClient {
    
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }
    
    val client: HttpClient = HttpClient(Android) {
        install(ContentNegotiation) {
            json(json)
        }
        
        install(Logging) {
            level = LogLevel.INFO
        }
        
        install(DefaultRequest) {
            headers.append("Content-Type", "application/json")
        }
        
        install(HttpTimeout) {
            requestTimeoutMillis = 30000
            connectTimeoutMillis = 10000
            socketTimeoutMillis = 30000
        }
    }
}
