package com.example.gamestore.ui.helpers

// TimeExtensions.kt

import java.util.concurrent.TimeUnit

fun Long.humanize(): String {
    val now = System.currentTimeMillis()
    val delta = now - this
    return when {
        delta < TimeUnit.MINUTES.toMillis(1)   -> "just now"
        delta < TimeUnit.HOURS.toMillis(1)     -> "${TimeUnit.MILLISECONDS.toMinutes(delta)} m ago"
        delta < TimeUnit.DAYS.toMillis(1)      -> "${TimeUnit.MILLISECONDS.toHours(delta)} h ago"
        else                                   -> "${TimeUnit.MILLISECONDS.toDays(delta)} d ago"
    }
}
