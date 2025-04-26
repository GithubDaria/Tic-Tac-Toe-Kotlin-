package com.example.lessontictactoe
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.*
import android.util.Log
import kotlin.math.log

class GameManager(var dim: Int = 3) {
    var field = mutableStateListOf(*Array(dim * dim) { "_" })
    var currentPlayerIndex by mutableStateOf(0)
  /*  var players = listOf(
        TicToePlayer("Player 1", symbol = "X"),
        TicToePlayer("Player 2", symbol = "O")
    )*/
  var players: List<TicToePlayer>
    var winner by mutableStateOf<String?>(null)
    var isGameOver by mutableStateOf(true)

    var timerValue by mutableStateOf(10)
    var isTimerRunning by mutableStateOf(false)
    var isFirstTurn = true  // Track whether it's the first turn
    var RoundCount by mutableStateOf(0);


    var aiPlayer: AIPlayer? = null

    var isAiMode: Boolean = false
        set(value) {
            field = value
            // Only change player 2 based on AI mode, keeping player 1 intact
            if (value) {
                // Create a new AI player and replace player 2
                aiPlayer = AIPlayer("AI", players[1].symbol, difficulty = Difficulty.IMPOSSIBLE)
                players = listOf(players[0], aiPlayer as TicToePlayer) // Replace player 2 with AI (safe cast)
            } else {
                // Replace AI with human player 2
                players = listOf(players[0], TicToePlayer("Player 2", symbol = "O"))
                aiPlayer = null // Remove AI player
            }
        }

    init {
        // Initialize player 1 and player 2 based on AI mode
        val player1 = TicToePlayer("Player 1", symbol = "X")
        val player2 = if (isAiMode) {
            AIPlayer("AI", symbol = "O", difficulty = Difficulty.IMPOSSIBLE).also { aiPlayer = it }
        } else {
            TicToePlayer("Player 2", symbol = "O")
        }

        players = listOf(player1, player2)
    }
    var difficulty: Difficulty = Difficulty.EASY
        private set

    fun changeDifficulty(newDifficulty: Difficulty) {
        Log.v("Players", "Player 1 symbol: ${players[0].symbol}, Player 2 symbol: ${players[1].symbol}")
        Log.v("Players", "Player 1 symbol: ${players[0].name}, Player 2 symbol: ${players[1].name}")
        isAiMode = true
        difficulty = newDifficulty
        Log.v("Players", "Player 1 symbol: ${players[0].symbol}, Player 2 symbol: ${players[1].symbol}")
        Log.v("Players", "Player 1 symbol: ${players[0].name}, Player 2 symbol: ${players[1].name}")
        // Create a new AIPlayer using Player 2's symbol
        val aiPlayerSymbol = players[1].symbol
        val realplayerSymbol = players[0].symbol
        // Create a new AIPlayer with the updated difficulty
        val aiPlayer = AIPlayer("AI", aiPlayerSymbol, difficulty = newDifficulty)

        // Replace Player 2 with the new AI player
        players = listOf(players[0], aiPlayer)
        Log.v("Players", "Player 1 symbol: ${players[0].symbol}, Player 2 symbol: ${players[1].symbol}")
        Log.v("Players", "Player 1 symbol: ${players[0].name}, Player 2 symbol: ${players[1].name}")

        Log.v("Players", "AI symbol = ${aiPlayer.symbol}")

        // If you have an AI player instance, update it as well
       // this.aiPlayer = ai
        if (checkIfAITurn()) {
            aiPlayer?.makeMove(this)
        }


    }

    fun checkIfAITurn(): Boolean {
        return players.getOrNull(currentPlayerIndex) is AIPlayer
    }

    fun switchToHumanPlayer() {
        // Replace AI with a normal human player (Player 2 in this case)
        val player1 = players[0]  // Keeping Player 1 the same
        val player2 = TicToePlayer("Player 2", symbol = players[1].symbol)  // Normal Player 2

        // Update players list with human player
        players = listOf(player1, player2)

        // Update the isAiMode to false, indicating we are no longer in AI mode
        isAiMode = false

        Log.d("Game", "Switched to human player. Player 2 is now human.")
    }



    fun makeMove(index: Int) {
        if (field[index] == "_" && winner == null) {

            field[index] = players[currentPlayerIndex].symbol
            winner = checkWinner()

            if (winner != null) {
                val winningPlayer = players.find { it.symbol == winner }
                winningPlayer?.score = winningPlayer?.score?.plus(1) ?: 0
                RoundCount++
                isGameOver = true
            } else {
                if (field.none { it == "_" }) {
                    winner = "Draw"
                    RoundCount++
                    isGameOver = true
                } else {
                    switchTurn() // always switch turn if not win/draw
                }
            }

            if (!isTimerRunning) {
                startTimer()
            }

        }
    }
    fun updatePlayerSymbol(index: Int, newSymbol: String) {
        // Get the old symbol of the player at the specified index
        val oldSymbol = players[index].symbol

        // If the new symbol is valid (single character)
        if (newSymbol.length == 1 && index in players.indices) {
            // Update the player's symbol
            players[index].symbol = newSymbol

            // Replace old symbol with the new symbol in the field
            field = field.map { cell ->
                if (cell == oldSymbol) newSymbol else cell // Replace old symbol with new
            }.toMutableStateList()

        }
    }

    fun resetBoardWithNewSize(newDim: Int) {
        dim = newDim
        field.clear()
        field.addAll(List(newDim * newDim) { "_" })
        // Reset other game state as needed (e.g., winner, timer)
    }


    fun updateTimer() {
        if (isTimerRunning && !isGameOver) {
            if (timerValue > 0) {
                timerValue-- // Decrease timer each second
            } else {
                switchTurn() // Switch turn if timer runs out
            }
        }
    }

    private fun switchTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size
        startTimer() // Reset timer for the new turn
        if (isAiMode && currentPlayerIndex == 1) {
            aiPlayer?.makeMove(this) // Let the AI make its move
        }
    }

    fun startTimer() {
        timerValue = 10 // Reset timer on each turn
        isTimerRunning = true
    }

    private fun checkWinner(): String? {
        // Check rows
        for (i in 0 until dim) {
            val start = i * dim
            val symbol = field[start]
            if (symbol != "_" && (1 until dim).all { field[start + it] == symbol }) {
                return symbol
            }
        }

        // Check columns
        for (i in 0 until dim) {
            val symbol = field[i]
            if (symbol != "_" && (1 until dim).all { field[i + it * dim] == symbol }) {
                return symbol
            }
        }

        // Check main diagonal
        val firstMainDiag = field[0]
        if (firstMainDiag != "_" && (1 until dim).all { field[it * (dim + 1)] == firstMainDiag }) {
            return firstMainDiag
        }

        // Check anti-diagonal
        val firstAntiDiag = field[dim - 1]
        if (firstAntiDiag != "_" && (1 until dim).all { field[(it + 1) * (dim - 1)] == firstAntiDiag }) {
            return firstAntiDiag
        }

        return null
    }


    fun reset() {
        // Reset the board with a fresh empty field
        Log.d("GameManager", "Board reset: $field")
        for (i in this.field.indices) field[i] = "_"
        Log.d("GameManager", "Board reset: $field")

        currentPlayerIndex = (currentPlayerIndex + 1) % players.size
        winner = null
        isTimerRunning = false
        timerValue = 10 // Reset the timer
        isFirstTurn = true // Reset the first turn flag
        isGameOver = false
        Log.d("GameManager", "Game reset. Current player: ${players[currentPlayerIndex].name}, Timer: $timerValue")
        if (isAiMode && this.currentPlayerIndex == 1) {
            aiPlayer?.makeMove(this) // Let the AI make its move
        }
        Log.d("GameManager", "Game reset. Current player: ${players[currentPlayerIndex].name}, Timer: $timerValue")

    }


    fun resetScores() {
        players.forEach { it.score = 0 }
        for (i in field.indices) field[i] = "_"
        currentPlayerIndex = 0
        winner = null
        isFirstTurn = true // Reset the first turn flag
        RoundCount = 0;
    }

    val currentPlayer get() = players[currentPlayerIndex]
    fun SetUpAi(){

    }

}


