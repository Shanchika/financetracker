package com.example.cashleaf.data

import android.content.Context
import android.content.SharedPreferences
import com.example.cashleaf.model.Budget
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREFERENCES_NAME, Context.MODE_PRIVATE
    )
    private val gson = Gson()

    companion object {
        private const val PREFERENCES_NAME = "finance_tracker_prefs"
        private const val KEY_CURRENCY = "currency"
        private const val KEY_BUDGET = "budget"
        private const val KEY_NOTIFICATION_ENABLED = "notification_enabled"
        private const val KEY_REMINDER_ENABLED = "reminder_enabled"
        private const val KEY_DARK_MODE_ENABLED = "dark_mode_enabled"
    }

    // Set currency to Sri Lankan Rupees (LKR)
    fun setCurrency(currency: String) {
        val currencyToStore = if (currency.isEmpty()) "LKR" else currency
        sharedPreferences.edit().putString(KEY_CURRENCY, currencyToStore).apply()
    }

    // Get the currency, defaulting to LKR if not set
    fun getCurrency(): String {
        return sharedPreferences.getString(KEY_CURRENCY, "USD") ?: "USD"
    }

    // Set Budget
    fun setBudget(budget: Budget) {
        val budgetJson = gson.toJson(budget.toMap())
        sharedPreferences.edit().putString(KEY_BUDGET, budgetJson).apply()
    }

    // Get Budget
    fun getBudget(): Budget {
        val budgetJson = sharedPreferences.getString(KEY_BUDGET, null)
        return if (budgetJson != null) {
            val type = object : TypeToken<Map<String, Any>>() {}.type
            val budgetMap: Map<String, Any> = gson.fromJson(budgetJson, type)
            Budget.fromMap(budgetMap)
        } else {
            Budget(0.0)  // Default budget is 0.0
        }
    }

    // Set if notifications are enabled
    fun setNotificationEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_NOTIFICATION_ENABLED, enabled).apply()
    }

    // Check if notifications are enabled
    fun isNotificationEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_NOTIFICATION_ENABLED, true)
    }

    // Set if reminders are enabled
    fun setReminderEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_REMINDER_ENABLED, enabled).apply()
    }

    // Check if reminders are enabled
    fun isReminderEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_REMINDER_ENABLED, false)
    }

    // Set dark mode preference
    fun setDarkModeEnabled(isEnabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_DARK_MODE_ENABLED, isEnabled).apply()
    }

    // Check if dark mode is enabled
    fun isDarkModeEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_DARK_MODE_ENABLED, false)
    }
}
//sdsds