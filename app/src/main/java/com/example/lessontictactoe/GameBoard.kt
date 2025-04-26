package com.example.lessontictactoe

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay

@Composable
fun GameBoard(gameManager: GameManager) {

    val boxSize = (300 / gameManager.dim).dp
    LaunchedEffect(gameManager.isTimerRunning) {
        while (gameManager.isTimerRunning) {
            delay(1000L)  // Wait for 1 second
            gameManager.updateTimer()  // Update the timer
        }
    }

    Column {
        for (row in 0 until gameManager.dim) {
            Row {
                for (col in 0 until gameManager.dim) {
                    val index = row * gameManager.dim + col
                    Box(
                        modifier = Modifier
                            .size(boxSize)
                            .padding(4.dp)
                            .background(Color(0xFF5A1E76))
                            .clickable { gameManager.makeMove(index) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = gameManager.field[index],
                            color = Color.White,
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                }
            }
        }


        Text(
            text = if (gameManager.winner != null) "Winner: ${gameManager.winner}" else "Turn: ${gameManager.players[gameManager.currentPlayerIndex].name}",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            modifier = Modifier.padding(top = 16.dp)
        )

   /*     Text(
            text = "Time left: ${gameManager.timerValue}s",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
            modifier = Modifier.padding(top = 8.dp)
        )*/

    }
}
