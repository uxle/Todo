package com.example.data

import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {
    val allTasks: Flow<List<Task>> = taskDao.getAllTasks()

    suspend fun insertTask(task: Task) = taskDao.insertTask(task)

    suspend fun deleteTaskById(id: String) = taskDao.deleteTaskById(id)
    
    suspend fun updateTaskStatus(id: String, isDone: Boolean) = taskDao.updateTaskStatus(id, isDone)
}
