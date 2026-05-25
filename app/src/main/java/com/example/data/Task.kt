package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey
    val id: String,
    val text: String,
    val done: Boolean = false,
    val date: String? = null,
    val flagged: Boolean = false,
    val created: Long = System.currentTimeMillis()
)
