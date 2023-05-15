package com.example.db.models

data class User(
    val id: Int,
    val login: String,
    val password: String,
    val fullName: String,
    val email: String
)