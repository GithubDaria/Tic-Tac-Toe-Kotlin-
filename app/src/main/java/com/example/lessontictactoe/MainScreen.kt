package com.example.lessontictactoe

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LayoutModifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lessontictactoe.ui.theme.LessonTicTacToeTheme
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextField
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.sp


@Preview(showBackground = true)
@Composable
fun MainScreenPreview()
{
    LessonTicTacToeTheme {
        MainScreen()
    }
}
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val gameManager = remember { GameManager() }
    var expanded by remember { mutableStateOf(false) }
    var selectedSize by remember { mutableStateOf("3x3") }
    var selectedDifficulty by remember { mutableStateOf("NO AI") }
    var Dificultyexpanded by remember { mutableStateOf(false) }
    val sizeOptions = listOf("3x3", "4x4", "5x5")
    val sizeMap = mapOf("3x3" to 3, "4x4" to 4, "5x5" to 5)
    /*
        LaunchedEffect(gameManager.players) {
            // This will run every time the players list changes
            Log.d("MainScreen", "Players updated: ${gameManager.players[0].score}, ${gameManager.players[1].score}")
        }
    */

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF5A1E76))
    ) {

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd) // 💡 Moved to left
                .padding(16.dp)
        ) {
            Button(onClick = { gameManager.resetScores() }) {
                Text("Restart")
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            // Dropdown menu
            Box (modifier = Modifier
                .padding(16.dp)
            ){
                Button(onClick = { expanded = true},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF43115B), // Correct background color
                        contentColor = Color.Black // Text color
                    ),
                    shape = MaterialTheme.shapes.small
                ) {


                    Text(
                        text = selectedSize,
                        color = Color.White, // White text color for contrast
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .background(Color(0xFF5A1E76)) // Custom background color, no rounding
                ) {
                    sizeOptions.forEach { option ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedSize = option
                                    expanded = false
                                    if (gameManager.isGameOver) {
                                        gameManager.resetBoardWithNewSize(sizeMap[option] ?: 3)
                                    }
                                }
                                .padding(vertical = 12.dp, horizontal = 16.dp) // Adjust padding
                        ) {
                            Text(
                                text = option,
                                color = Color.White, // Text color to stand out against the background
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }

            }
        }
        // Difficulty Dropdown menu
        Box(
            modifier = Modifier
                .padding(16.dp)
                .offset(x = 80.dp) // Moves the Box 40dp to the right
        ) {
            Button(
                onClick = { Dificultyexpanded = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF43115B), // Background color
                    contentColor = Color.Black // Unused but included
                ),
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = selectedDifficulty,
                    color = Color.White, // White text
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            DropdownMenu(
                expanded = Dificultyexpanded,
                onDismissRequest = { Dificultyexpanded = false },
                modifier = Modifier
                    .background(Color(0xFF5A1E76)) // Custom background
            ) {
                val difficultyOptions = listOf("NO AI", "EASY", "HARD", "IMPOSSIBLE")

                difficultyOptions.forEach { option ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedDifficulty = option
                                Dificultyexpanded = false
                                val selectedOption = option.uppercase()

                                if (selectedOption == "NO AI") {
                                    gameManager.switchToHumanPlayer()
                                } else {
                                    // Convert the string to Difficulty enum
                                    val difficultyEnum = try {
                                        Difficulty.valueOf(selectedOption)
                                    } catch (e: IllegalArgumentException) {
                                        // Handle invalid difficulty, e.g. print a log or fallback to a default
                                        null
                                    }

                                    if (difficultyEnum != null) {
                                        gameManager.changeDifficulty(difficultyEnum)
                                    }
                                }
                            }
                            .padding(vertical = 12.dp, horizontal = 16.dp)
                    ) {
                        Text(
                            text = option,
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }

        /*        Text(
                    text = "Tic Tac Toe",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 8.dp)
                )*/
/*        Text(
            text = "${gameManager.players[0].name}: ${gameManager.players[0].score}  |  ${gameManager.players[1].name}: ${gameManager.players[1].score}",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )*/

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .background(Color(0xFF2A0A4A), shape = RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 20.dp) // Adjust the padding as needed
        ){


            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp) // Adjust vertical padding to make it smaller
            ) {
                // Your content inside the Column

            Row(
                    horizontalArrangement = Arrangement.spacedBy(0.dp, Alignment.CenterHorizontally),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 25.dp, start = 0.dp, end = 0.dp)
                ) {
                    // PlayerScoreBox for Player 1, Player 2, and Draws
                    PlayerScoreBox(playerName = gameManager.players[0].name, score = gameManager.players[0].score, backgroundColor = Color(0xFF48D2FE) )
                    PlayerScoreBox(
                        playerName = "Draws",
                        score = gameManager.RoundCount - (gameManager.players[0].score + gameManager.players[1].score),
                        backgroundColor = Color(0xFFC8E6C9)
                    )
                    PlayerScoreBox(playerName = gameManager.players[1].name, score = gameManager.players[1].score, backgroundColor = Color(0xFFE2BE00))

                }
                Text(
                    text = "${gameManager.timerValue}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 10.dp, top = 10.dp)
                )

                GameBoard(gameManager = gameManager)// 👈 Pass it here

                Button(
                    onClick = { gameManager.reset() },
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .width((300.dp)) // Make the button wider
                        .height(50.dp), // Adjust the height as needed
                    enabled = gameManager.isGameOver,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF4F6F5), // Correct background color
                        contentColor = Color.Black // Text color
                    ),
                    shape = MaterialTheme.shapes.small // Less rounded corners
                ) {
                    Text(
                        text = "New Game",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

            }


        }
        Box(
            modifier = Modifier
                .fillMaxSize() // Fill the screen
                .padding(0.dp) // Optional: Add padding from the edge
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart) // Align at the bottom-left corner
                    .padding(0.dp),
                    verticalArrangement = Arrangement.spacedBy(-12.dp)
            ) {
                Text(
                    text = "tic",
                    color = Color(0xFFDCBF3F),
                    fontWeight = FontWeight.Bold, // Make the text bold
                    style = MaterialTheme.typography.headlineLarge
                )
                Text(
                    text = "tac",
                    color = Color(0xFF72CFF9),
                    fontWeight = FontWeight.Bold, // Make the text bold
                    style = MaterialTheme.typography.headlineLarge
                )
                Text(
                    text = "toe",
                    color = Color(0xFFDCBF3F),
                    fontWeight = FontWeight.Bold, // Make the text bold
                    style = MaterialTheme.typography.headlineLarge
                )
            }
        }

        // Call the input UI here
        BottomRightHorizontalButtonsWithInputs(gameManager = gameManager)
    }
}

@Composable
fun PlayerScoreBox(playerName: String, score: Int, backgroundColor: Color) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .background(backgroundColor, shape = RoundedCornerShape(5.dp))
            .size(width = 75.dp, height = 75.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = playerName,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Black
            )
            Text(
                text = "$score",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = Color.Black
            )
        }
    }
}
@Composable
fun GridSizeDropdown(selectedSize: Int, onSizeSelected: (Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf(3, 4, 5)
    val selectedText = "${selectedSize}x$selectedSize"

    Box(modifier = Modifier
        .padding(16.dp)
        .wrapContentSize(Alignment.TopStart)) {

        Text(
            text = selectedText,
            modifier = Modifier
                .background(Color(0xFFF4F6F5), shape = RoundedCornerShape(8.dp))
                .clickable { expanded = true }
                .padding(horizontal = 16.dp, vertical = 10.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { size ->
                DropdownMenuItem(
                    text = { Text("${size}x$size") },
                    onClick = {
                        onSizeSelected(size)
                        expanded = false
                    }
                )
            }
        }
    }
}
@Composable
fun BottomRightHorizontalButtonsWithInputs(gameManager: GameManager) {
    var showInput1 by remember { mutableStateOf(false) }
    var showInput2 by remember { mutableStateOf(false) }
    var text1 by remember { mutableStateOf("") }
    var text2 by remember { mutableStateOf("") }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(horizontalAlignment = Alignment.End) {
                Button(
                    onClick = { showInput1 = !showInput1 },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF43115B),
                        contentColor = Color(0xFF5A1E76)
                    ),
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.size(width = 80.dp, height = 36.dp)
                ) {
                    Text("P 1: ${gameManager.players[0].symbol}", fontSize = MaterialTheme.typography.labelSmall.fontSize)
                }

                if (showInput1) {
                    TextField(
                        value = text1,
                        onValueChange = {
                            if (it.length <= 1) text1 = it // or text2 for the second input
                        },
                                modifier = Modifier
                            .padding(top = 4.dp)
                            .width(100.dp)
                            .height(50.dp),
                        textStyle = LocalTextStyle.current.copy(
                            fontSize = 18.sp, // Set a larger font size
                            color = Color(0xFF43115B) // Your desired text color
                        )
                        ,
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            showInput1 = false
                            gameManager.updatePlayerSymbol(0, text1)}),
                        singleLine = true
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Button(
                    onClick = { showInput2 = !showInput2 },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF43115B),
                        contentColor = Color(0xFF5A1E76)
                    ),
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.size(width = 80.dp, height = 36.dp)
                ) {
                    Text("P 2: ${gameManager.players[1].symbol}", fontSize = MaterialTheme.typography.labelSmall.fontSize)
                }

                if (showInput2) {
                    TextField(
                        value = text2,
                        onValueChange = {
                            if (it.length <= 1) text2 = it // or text2 for the second input
                        },
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .width(100.dp)
                            .height(50.dp),

                        textStyle = LocalTextStyle.current.copy(
                            fontSize = 18.sp, // Set a larger font size
                            color = Color(0xFF43115B) // Your desired text color
                        )
                        ,
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            showInput2 = false
                            gameManager.updatePlayerSymbol(1, text2) }),
                        singleLine = true
                    )
                }
            }
        }
    }
}





