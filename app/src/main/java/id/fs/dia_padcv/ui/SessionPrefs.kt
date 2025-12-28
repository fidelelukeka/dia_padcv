package id.fs.dia_padcv.ui

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore("session_prefs")

object SessionPrefs {
    suspend fun saveLogin(context: Context, username: String, passwordHash: String, role: String?) {
        Log.d("SessionPrefs", "▶️ Début sauvegarde session: username=$username, passwordHash=$passwordHash, role=$role")
        context.dataStore.edit { prefs ->
            prefs[stringPreferencesKey("username")] = username.trim()
            prefs[stringPreferencesKey("password_hash")] = passwordHash.trim()
            prefs[stringPreferencesKey("role")] = role ?: "unknown"
            prefs[stringPreferencesKey("isLoggedIn")] = "true"
        }
        Log.d("SessionPrefs", "✅ Session sauvegardée: $username / $role")
    }

    fun getStoredCredentials(context: Context): Flow<Pair<String?, String?>> {
        Log.d("SessionPrefs", "📂 Lecture des credentials stockés")
        return context.dataStore.data.map { prefs ->
            val username = prefs[stringPreferencesKey("username")]
            val passwordHash = prefs[stringPreferencesKey("password_hash")]
            Log.d("SessionPrefs", "🔎 Credentials lus: username=$username, passwordHash=$passwordHash")
            Pair(username, passwordHash)
        }
    }
}