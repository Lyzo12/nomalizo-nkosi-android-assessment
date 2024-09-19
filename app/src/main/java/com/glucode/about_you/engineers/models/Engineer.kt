package com.glucode.about_you.engineers.models

data class Engineer(
    val name: String,
    val role: String,
    var defaultImageName: String,
    val quickStats: QuickStats,
    val questions: List<Question>,
)