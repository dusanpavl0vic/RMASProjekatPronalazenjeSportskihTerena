package com.example.sportfields.screens

import com.example.sportfields.R

val fieldTypeIcons = mapOf(
    "tennis" to R.drawable.tennis,
    "football" to R.drawable.football,
    "basketball" to R.drawable.basketball,
    "handball" to R.drawable.handball,
    "volleyball" to R.drawable.volleyball,
)

enum class FieldType(val displayName: String) {
    FUDBAL("Fudbal"),
    TENIS("Tenis"),
    KOSARKA("Kosarka"),
    RUKOMET("Rukomet"),
    ODBOJKA("Odbojka")
}