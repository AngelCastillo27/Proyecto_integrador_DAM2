package com.example.inventario_pi_v1.network

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import java.util.Locale

object LocaleHelper {
    private const val PREFS_NAME = "user_prefs"
    private const val KEY_LANG = "app_lang"

    // Se llama en attachBaseContext de cada Activity
    fun onAttach(context: Context): Context {
        val lang = getPersistedLanguage(context)
        return setLocale(context, lang)
    }

    fun setLocale(context: Context, languageCode: String): Context {
        persistLanguage(context, languageCode)

        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val resources = context.resources
        val config = Configuration(resources.configuration)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale)
        } else {
            @Suppress("DEPRECATION")
            config.locale = locale
        }

        // Sincronización agresiva de recursos
        @Suppress("DEPRECATION")
        resources.updateConfiguration(config, resources.displayMetrics)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            context.createConfigurationContext(config)
        } else {
            context
        }
    }

    fun getPersistedLanguage(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        // FORZADO: Siempre Español al inicio si no hay nada guardado
        return prefs.getString(KEY_LANG, "es") ?: "es"
    }

    private fun persistLanguage(context: Context, languageCode: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
        prefs.putString(KEY_LANG, languageCode)
        // commit() es síncrono -> evita el error del doble clic
        prefs.commit()
    }
}