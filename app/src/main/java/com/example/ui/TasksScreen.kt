package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Task
import com.example.ui.theme.*
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(viewModel: TasksViewModel, onToggleTheme: () -> Unit, isDarkTheme: Boolean) {
    val tasks by viewModel.uiState.collectAsStateWithLifecycle()
    val xp by viewModel.xp.collectAsStateWithLifecycle()
    val level by viewModel.level.collectAsStateWithLifecycle()
    val rankTitle by viewModel.rankTitle.collectAsStateWithLifecycle()
    val streak by viewModel.streak.collectAsStateWithLifecycle()

    var inputText by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(CyberDark)
                .imePadding()
        ) {
            // Cyber Grid Background
            CyberGrid(modifier = Modifier.fillMaxSize())

            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val isWide = maxWidth > 800.dp

                if (isWide) {
                    // PC / Tablet Layout
                    Row(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            CyberDashboard(level, rankTitle, xp, streak, viewModel.getXpProgress())
                        }
                        Column(modifier = Modifier.weight(2f)) {
                            MissionInput(
                                inputText = inputText,
                                onInputChange = { inputText = it },
                                onSubmit = {
                                    viewModel.addTask(inputText)
                                    inputText = ""
                                    keyboardController?.hide()
                                }
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            MissionList(tasks, viewModel)
                        }
                    }
                } else {
                    // Mobile Layout
                    Column(
                        modifier = Modifier.fillMaxSize().padding(16.dp)
                    ) {
                        CyberDashboard(level, rankTitle, xp, streak, viewModel.getXpProgress())
                        Spacer(modifier = Modifier.height(16.dp))
                        MissionInput(
                            inputText = inputText,
                            onInputChange = { inputText = it },
                            onSubmit = {
                                viewModel.addTask(inputText)
                                inputText = ""
                                keyboardController?.hide()
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        MissionList(tasks, viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun CyberGrid(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val step = 100f
        val color = CyberCyan.copy(alpha = 0.05f)
        for (x in 0..size.width.toInt() step step.toInt()) {
            drawLine(color, Offset(x.toFloat(), 0f), Offset(x.toFloat(), size.height), strokeWidth = 2f)
        }
        for (y in 0..size.height.toInt() step step.toInt()) {
            drawLine(color, Offset(0f, y.toFloat()), Offset(size.width, y.toFloat()), strokeWidth = 2f)
        }
    }
}

@Composable
fun CyberDashboard(level: Int, rank: String, xp: Int, streak: Int, progress: Float) {
    Card(
        shape = CutCornerShape(topStart = 16.dp, bottomEnd = 16.dp),
        colors = CardDefaults.cardColors(containerColor = CyberSurface.copy(alpha = 0.8f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth().shadow(8.dp, CutCornerShape(16.dp))
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "SYSTEM DASHBOARD",
                color = CyberCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "LVL $level", color = CyberTextPrimary, fontSize = 32.sp, fontWeight = FontWeight.Black)
                    Text(text = rank.uppercase(), color = CyberPink, fontSize = 14.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "XP", color = CyberTextSecondary, fontSize = 12.sp)
                    Text(text = "$xp", color = CyberSuccess, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(CyberSurfaceVariant, CutCornerShape(4.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .height(8.dp)
                        .background(
                            brush = Brush.horizontalGradient(listOf(CyberCyan, CyberSuccess)),
                            shape = CutCornerShape(4.dp)
                        )
                )
            }
        }
    }
}

@Composable
fun MissionInput(inputText: String, onInputChange: (String) -> Unit, onSubmit: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CyberSurface.copy(alpha = 0.8f), CutCornerShape(12.dp))
            .border(1.dp, CyberCyan.copy(alpha = 0.3f), CutCornerShape(12.dp))
            .padding(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = inputText,
            onValueChange = onInputChange,
            modifier = Modifier.weight(1f).heightIn(min = 52.dp),
            placeholder = { Text("ASSIGN NEW MISSION...", color = CyberTextSecondary.copy(alpha = 0.5f)) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = CyberTextPrimary,
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { onSubmit() }),
            singleLine = true
        )
        IconButton(
            onClick = onSubmit,
            modifier = Modifier
                .size(40.dp)
                .background(CyberCyan, CutCornerShape(8.dp)),
            colors = IconButtonDefaults.iconButtonColors(contentColor = CyberDark)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Create Mission")
        }
    }
}

@Composable
fun MissionList(tasks: List<Task>, viewModel: TasksViewModel) {
    if (tasks.isEmpty()) {
        Box(modifier = Modifier.fillMaxWidth().padding(top = 48.dp), contentAlignment = Alignment.Center) {
            Text(text = "NO ACTIVE MISSIONS", color = CyberTextSecondary, letterSpacing = 2.sp)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(tasks, key = { it.id }) { task ->
                MissionItem(
                    task = task,
                    onToggle = { viewModel.toggleTask(task.id, task.done) },
                    onDelete = { viewModel.deleteTask(task.id) }
                )
            }
        }
    }
}

@Composable
fun MissionItem(task: Task, onToggle: () -> Unit, onDelete: () -> Unit) {
    Card(
        shape = CutCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (task.done) CyberDark.copy(alpha = 0.6f) else CyberSurfaceVariant.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (task.done) 0.dp else 4.dp),
        modifier = Modifier.fillMaxWidth().clickable { onToggle() },
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (task.done) CyberSuccess.copy(alpha = 0.3f) else CyberCyan.copy(alpha = 0.4f)
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(if (task.done) CyberSuccess else Color.Transparent, CutCornerShape(4.dp))
                    .border(1.dp, if (task.done) CyberSuccess else CyberCyan, CutCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = task.done,
                    enter = scaleIn(tween(200)),
                    exit = scaleOut(tween(200))
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Done", tint = CyberDark, modifier = Modifier.size(16.dp))
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.text,
                    color = if (task.done) CyberTextSecondary else CyberTextPrimary,
                    textDecoration = if (task.done) TextDecoration.LineThrough else null,
                    fontSize = 16.sp
                )
            }

            IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.Close, contentDescription = "Delete", tint = CyberDanger)
            }
        }
    }
}
