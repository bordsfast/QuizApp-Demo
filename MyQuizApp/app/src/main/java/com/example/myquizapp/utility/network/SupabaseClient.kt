package com.example.myquizapp.utility.network

import com.example.myquizapp.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime

/**
 * SupabaseClient.kt - Supabase client configuration
 * Credentials are loaded from BuildConfig (defined in gradle.properties)
 */
object SupabaseClientProvider {

    private var client: SupabaseClient? = null

    fun getClient(): SupabaseClient {
        return client ?: createClient().also { client = it }
    }

    private fun createClient(): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_ANON_KEY
        ) {
            install(Postgrest)
            install(Realtime)
        }
    }
}
