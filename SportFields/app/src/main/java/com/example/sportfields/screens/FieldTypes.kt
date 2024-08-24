package com.example.sportfields.screens

import com.example.sportfields.R

val fieldTypeIcons = mapOf(
    "TENIS" to R.drawable.tennis,
    "FUDBAL" to R.drawable.football,
    "KOSARKA" to R.drawable.basketball,
    "RUKOMET" to R.drawable.handball,
    "ODBOJKA" to R.drawable.volleyball,
)

enum class FieldType(val displayName: String) {
    FUDBAL("FUDBAL"),
    TENIS("TENIS"),
    KOSARKA("KOSARKA"),
    RUKOMET("RUKOMET"),
    ODBOJKA("ODBOJKA")
}