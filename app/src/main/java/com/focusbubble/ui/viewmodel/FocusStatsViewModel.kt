package com.focusbubble.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.temporal.WeekFields
import java.util.Locale

class FocusStatsViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("focus_stats", Context.MODE_PRIVATE)

    private val _weeklyFocusTime = MutableStateFlow(0) // in minutes
    val weeklyFocusTime: StateFlow<Int> = _weeklyFocusTime

    private var currentWeek: Int

    init {
        val savedWeek = prefs.getInt("current_week", -1)
        val savedTime = prefs.getInt("weekly_time", 0)

        val nowWeek = LocalDate.now()
            .get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear())

        currentWeek = nowWeek

        if (savedWeek == nowWeek) {
            _weeklyFocusTime.value = savedTime
        } else {
            _weeklyFocusTime.value = 0
            saveToPrefs()
        }
    }

    fun addFocusTime(minutes: Int) {
        viewModelScope.launch {
            val nowWeek = LocalDate.now()
                .get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear())

            if (nowWeek != currentWeek) {
                currentWeek = nowWeek
                _weeklyFocusTime.value = 0
            }

            _weeklyFocusTime.value += minutes
            saveToPrefs()
        }
    }

    private fun saveToPrefs() {
        prefs.edit()
            .putInt("current_week", currentWeek)
            .putInt("weekly_time", _weeklyFocusTime.value)
            .apply()
    }
}
