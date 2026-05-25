package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.Task
import com.example.data.TaskRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.regex.Pattern

class TasksViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TaskRepository

    val uiState: StateFlow<List<Task>>

    init {
        val taskDao = AppDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(taskDao)

        uiState = repository.allTasks
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

    private val dateTodayPattern = Pattern.compile("\\b(today)\\b", Pattern.CASE_INSENSITIVE)
    private val dateTomorrowPattern = Pattern.compile("\\b(tomorrow|tmrw)\\b", Pattern.CASE_INSENSITIVE)
    private val dateNextWeekPattern = Pattern.compile("\\b(next week)\\b", Pattern.CASE_INSENSITIVE)
    private val priorityHighPattern = Pattern.compile("\\b(!high|!important|!urgent)\\b", Pattern.CASE_INSENSITIVE)

    fun addTask(rawText: String) {
        val trimmed = rawText.trim()
        if (trimmed.isEmpty()) return

        var cleanText = trimmed
        var date: String? = null
        var flagged = false

        if (priorityHighPattern.matcher(cleanText).find()) {
            flagged = true
            cleanText = cleanText.replace(priorityHighPattern.toRegex(), "")
        }

        val today = java.time.LocalDate.now()
        
        if (dateTodayPattern.matcher(cleanText).find()) {
            date = today.toString()
            cleanText = cleanText.replace(dateTodayPattern.toRegex(), "")
        } else if (dateTomorrowPattern.matcher(cleanText).find()) {
            date = today.plusDays(1).toString()
            cleanText = cleanText.replace(dateTomorrowPattern.toRegex(), "")
        } else if (dateNextWeekPattern.matcher(cleanText).find()) {
            date = today.plusDays(7).toString()
            cleanText = cleanText.replace(dateNextWeekPattern.toRegex(), "")
        }

        val task = Task(
            id = UUID.randomUUID().toString(),
            text = cleanText.trim(),
            date = date,
            flagged = flagged
        )

        viewModelScope.launch {
            repository.insertTask(task)
        }
    }

    fun toggleTask(id: String, currentStatus: Boolean) {
        viewModelScope.launch {
            repository.updateTaskStatus(id, !currentStatus)
        }
    }

    fun deleteTask(id: String) {
        viewModelScope.launch {
            repository.deleteTaskById(id)
        }
    }
}
