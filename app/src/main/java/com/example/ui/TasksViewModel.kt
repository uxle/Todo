package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.Task
import com.example.data.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class TasksViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TaskRepository
    val uiState: StateFlow<List<Task>>

    // RPG System
    private val prefs = application.getSharedPreferences("rpg_stats", Context.MODE_PRIVATE)
    
    private val _xp = MutableStateFlow(prefs.getInt("xp", 0))
    val xp: StateFlow<Int> = _xp
    
    private val _level = MutableStateFlow(calculateLevel(_xp.value))
    val level: StateFlow<Int> = _level
    
    private val _rankTitle = MutableStateFlow(calculateRankTitle(_level.value))
    val rankTitle: StateFlow<String> = _rankTitle
    
    private val _streak = MutableStateFlow(prefs.getInt("streak", 0))
    val streak: StateFlow<Int> = _streak

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

    fun addTask(rawText: String) {
        val trimmed = rawText.trim()
        if (trimmed.isEmpty()) return

        val task = Task(
            id = UUID.randomUUID().toString(),
            text = trimmed
        )

        viewModelScope.launch {
            repository.insertTask(task)
        }
    }

    fun toggleTask(id: String, currentStatus: Boolean) {
        viewModelScope.launch {
            val newStatus = !currentStatus
            repository.updateTaskStatus(id, newStatus)
            
            if (newStatus) {
                // Task completed! Award XP
                awardXp(50)
            } else {
                // Task uncompleted (optional penalty, let's keep it simple and just deduct what was awarded previously or skip it to be generous)
            }
        }
    }

    fun deleteTask(id: String) {
        viewModelScope.launch {
            repository.deleteTaskById(id)
        }
    }

    private fun awardXp(amount: Int) {
        val newXp = _xp.value + amount
        _xp.value = newXp
        prefs.edit().putInt("xp", newXp).apply()
        
        val newLevel = calculateLevel(newXp)
        if (newLevel != _level.value) {
            _level.value = newLevel
            _rankTitle.value = calculateRankTitle(newLevel)
        }
    }
    
    // --- RPG Logic ---
    private fun calculateLevel(currentXp: Int): Int {
        // Level increases every 100 * Level XP (e.g., Level 1 -> Level 2 requires 100 XP, 2 -> 3 requires 300)
        // Let's use a simpler linear scale for demo: Level = (XP / 200) + 1
        return (currentXp / 200) + 1
    }
    
    fun getXpProgress(): Float {
        val xpForCurrentLevel = (_level.value - 1) * 200
        val xpForNextLevel = _level.value * 200
        val currentLevelXp = _xp.value - xpForCurrentLevel
        val requiredXp = xpForNextLevel - xpForCurrentLevel
        return currentLevelXp.toFloat() / requiredXp.toFloat()
    }

    private fun calculateRankTitle(lvl: Int): String {
        return when {
            lvl < 2 -> "Beginner"
            lvl < 4 -> "Hunter"
            lvl < 8 -> "Elite"
            lvl < 15 -> "Shadow Monarch"
            lvl < 25 -> "Architect"
            else -> "Titan"
        }
    }
}
