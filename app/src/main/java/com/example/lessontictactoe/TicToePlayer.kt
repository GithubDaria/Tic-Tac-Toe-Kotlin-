package com.example.lessontictactoe
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

open class TicToePlayer(
    name: String,
    initialScore: Int = 0,
    var symbol: String
) {
    var name by mutableStateOf(name)
    var score by mutableStateOf(initialScore)
}

