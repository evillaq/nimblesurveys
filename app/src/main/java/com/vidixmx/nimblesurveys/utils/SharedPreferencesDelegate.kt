package com.vidixmx.nimblesurveys.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class SharedPreferencesDelegate(
    private val context: Context,
    private val name: String,
    private val defaultValue: String = "",

    ) : ReadWriteProperty<Any, String> {

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences("nimblesurveys_prefs", Context.MODE_PRIVATE)
    }

    override fun getValue(thisRef: Any, property: KProperty<*>): String {
        return sharedPreferences.getString(name, defaultValue) ?: defaultValue
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: String) {
        sharedPreferences.edit().putString(name, value).apply()
    }

}

fun Context.sharedPreferences(name: String, defaultValue: String = "") =
    SharedPreferencesDelegate(this, name, defaultValue)

fun AndroidViewModel.sharedPreferences(name: String, defaultValue: String = "") =
    SharedPreferencesDelegate(this.getApplication(), name, defaultValue)