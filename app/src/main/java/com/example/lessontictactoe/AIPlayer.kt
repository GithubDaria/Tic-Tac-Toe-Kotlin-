package com.example.lessontictactoe
import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlin.random.Random

enum class Difficulty {
    EASY, HARD, IMPOSSIBLE
}
class AIPlayer(
    name: String,
    symbol: String,
    var difficulty: Difficulty

) : TicToePlayer(name,  0, symbol) {

    fun makeMove(gameManager: GameManager) {
        when (difficulty) {
            Difficulty.EASY -> makeEasyMove(gameManager)
            Difficulty.HARD -> {
                if (Random.nextBoolean()) {
                    makeEasyMove(gameManager)
                } else {
                    makeImpossibleMove(gameManager)
                }
            }
            Difficulty.IMPOSSIBLE -> makeImpossibleMove(gameManager)
        }
    }

    private fun makeEasyMove(gameManager: GameManager) {
        val board = gameManager.field
        val aiSymbol = symbol
        val opponentSymbol = board.firstOrNull { it != aiSymbol && it != "_" } ?: "X"

        val availableMoves = board.indices.filter { board[it] == "_" }

        // 70% chance to play random move
        if ((0..100).random() < 70) {
            gameManager.makeMove(availableMoves.random())
            return
        }

        // 30% smart chance: try to win or block
        findWinningMove(board, gameManager.dim, gameManager.dim, aiSymbol, gameManager.dim)?.let {
            gameManager.makeMove(it)
            return
        }

        findWinningMove(board, gameManager.dim, gameManager.dim, opponentSymbol, gameManager.dim)?.let {
            gameManager.makeMove(it)
            return
        }

        // If no win/block possible, fallback to random
        gameManager.makeMove(availableMoves.random())
    }
   /* private fun makeHardMove(gameManager: GameManager) {
        val board = gameManager.field
        val aiSymbol = symbol

        val opponentSymbol = board.firstOrNull { it != aiSymbol && it != "_" } ?: return

        // 1. Try to win
        findWinningMove(board, gameManager.dim, gameManager.dim, aiSymbol, gameManager.dim)?.let {
            gameManager.makeMove(it)
            return
        }

        // 2. Block opponent with high chance
        if ((0..100).random() < 85) { // 85% chance to block
            findWinningMove(board, gameManager.dim, gameManager.dim, opponentSymbol, gameManager.dim)?.let {
                gameManager.makeMove(it)
                return
            }
        }

        // 3. Take center with 75% chance
        val center = (gameManager.dim * gameManager.dim) / 2
        if (board[center] == "_" && (0..100).random() < 75) {
            gameManager.makeMove(center)
            return
        }

        // 4. Prefer corner, but sometimes ignore it
        val corners = listOf(0, gameManager.dim - 1, board.size - gameManager.dim, board.size - 1)
        val availableCorners = corners.filter { board[it] == "_" }
        if (availableCorners.isNotEmpty() && (0..100).random() < 80) {
            gameManager.makeMove(availableCorners.random())
            return
        }

        // 5. Random fallback
        val availableMoves = board.indices.filter { board[it] == "_" }
        if (availableMoves.isNotEmpty()) {
            gameManager.makeMove(availableMoves.random())
        }
    }*/


    private fun makeImpossibleMove(gameManager: GameManager) {
        val board = gameManager.field
        val aiSymbol = symbol

        val opponentSymbol = board.firstOrNull { it != aiSymbol && it != "_" }?.let { it }

        if (opponentSymbol == null) {
            // No opponent's symbol found, which means AI is the first to play
            val corners = listOf(0, gameManager.dim - 1, board.size - gameManager.dim, board.size - 1)
            val availableCorners = corners.filter { board[it] == "_" }

            if (availableCorners.isNotEmpty()) {
                val randomCorner = availableCorners.random()
                gameManager.makeMove(randomCorner)  // Make a move in one of the available corners
                return
            }
        }

        else{

            // 1. Win if possible
            findWinningMove(board, gameManager.dim, gameManager.dim, aiSymbol, gameManager.dim)?.let {
                gameManager.makeMove(it)
                return
            }

            // 2. Block opponent's win
            findWinningMove(board, gameManager.dim, gameManager.dim, opponentSymbol, gameManager.dim)?.let {
                gameManager.makeMove(it)
                return
            }
            // 3. Create a fork if possible
            findForkMove(board, gameManager.dim, aiSymbol)?.let {
                gameManager.makeMove(it)
                return
            }
            // 4. Block opponent's fork
            opponentSymbol?.let {
                findForkMove(board, gameManager.dim, it)?.let { forkMove ->
                    gameManager.makeMove(forkMove)
                    return
                }
            }
            // 4. Take a corner if available
            val corners = listOf(0, gameManager.dim - 1, board.size - gameManager.dim, board.size - 1)
            val availableCorners = corners.filter { board[it] == "_" }
            if (availableCorners.isNotEmpty()) {
                gameManager.makeMove(availableCorners.random())
                return
            }
            // 3. Take center if available
            val center = (gameManager.dim * gameManager.dim) / 2
            if (board[center] == "_") {
                gameManager.makeMove(center)
                return
            }


            // 5. Take any side
            val sides = board.indices.filter { board[it] == "_" }
            if (sides.isNotEmpty()) {
                gameManager.makeMove(sides.random())
            }
        }
    }

    private fun findWinningMove(
        board: List<String>,
        boardWidth: Int,
        boardHeight: Int,
        playerSymbol: String,
        winLength: Int
    ): Int? {
        val directions = listOf(
            Pair(1, 0),  // Horizontal direction (left-right)
            Pair(0, 1),  // Vertical direction (top-bottom)
            Pair(1, 1),  // Diagonal direction (top-left to bottom-right)
            Pair(1, -1)  // Diagonal direction (top-right to bottom-left)
        )

        // Loop through each cell on the board
        for (rowIndex in 0 until boardHeight) {
            for (columnIndex in 0 until boardWidth) {
                val cellIndex = rowIndex * boardWidth + columnIndex  // Convert 2D coordinates to 1D index

                // Only consider empty cells ("_")
                if (board[cellIndex] == "_") {
                    // Log the current cell being considered
                    Log.d("AI", "Checking cell ($rowIndex, $columnIndex)")

                    // Check all directions for a possible winning move
                    for ((deltaX, deltaY) in directions) {
                        var consecutiveCount = 1  // Start counting the current cell as part of the sequence
                        var nextX = columnIndex + deltaX
                        var nextY = rowIndex + deltaY

                        // Check in the positive direction (right, down, diagonal, etc.)
                        while (nextX in 0 until boardWidth && nextY in 0 until boardHeight &&
                            board[nextY * boardWidth + nextX] == playerSymbol) {
                            consecutiveCount++
                            nextX += deltaX
                            nextY += deltaY
                        }

                        // Check in the negative direction (left, up, diagonal, etc.)
                        nextX = columnIndex - deltaX
                        nextY = rowIndex - deltaY
                        while (nextX in 0 until boardWidth && nextY in 0 until boardHeight &&
                            board[nextY * boardWidth + nextX] == playerSymbol) {
                            consecutiveCount++
                            nextX -= deltaX
                            nextY -= deltaY
                        }

                        // Log the number of consecutive symbols found in this direction
                        Log.d("AI", "Found $consecutiveCount consecutive $playerSymbol in direction ($deltaX, $deltaY)")

                        // If we found a winning sequence
                        if (consecutiveCount >= winLength) {
                            Log.d("AI", "Found winning move at ($rowIndex, $columnIndex)")
                            return cellIndex  // Return the index of the winning move
                        }
                    }
                }
            }
        }

        Log.d("AI", "No winning move found")
        return null  // Return null if no winning move is found
    }

    private fun findForkMove(board: List<String>, dim: Int, symbol: String): Int? {
        val forkMoves = mutableListOf<Int>()

        for (i in board.indices) {
            if (board[i] != "_") continue

            val tempBoard = board.toMutableList()
            tempBoard[i] = symbol

            var winningMoves = 0
            for (j in tempBoard.indices) {
                if (tempBoard[j] == "_") {
                    val testBoard = tempBoard.toMutableList()
                    testBoard[j] = symbol

                    if (isWinningBoard(testBoard, dim, symbol)) {
                        winningMoves++
                    }
                }
            }

            if (winningMoves >= 2) {
                forkMoves.add(i)
            }
        }

        return forkMoves.firstOrNull()
    }
    private fun isWinningBoard(board: List<String>, dim: Int, symbol: String): Boolean {
        val winLines = mutableListOf<List<Int>>()

        // Rows
        for (i in 0 until dim) {
            winLines.add((0 until dim).map { i * dim + it })
        }

        // Columns
        for (i in 0 until dim) {
            winLines.add((0 until dim).map { it * dim + i })
        }

        // Diagonals
        winLines.add((0 until dim).map { it * (dim + 1) })
        winLines.add((1..dim).map { it * (dim - 1) }.filter { it < board.size })

        return winLines.any { line -> line.all { board[it] == symbol } }
    }

}
