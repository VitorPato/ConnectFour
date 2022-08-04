package connectfour

import kotlin.system.exitProcess

val PLAYER_CHARACTER = listOf('o', '*')
const val QUIT_GAME = 100
const val PLAYER_1_VICTORY = 'o'
const val PLAYER_2_VICTORY = '*'
const val GAME_IS_A_DRAW = 'd'
const val GAME_NOT_OVER = ' '

const val UPPER_LENGTH = 9
const val LOWER_LENGTH = 5
const val UPPER_WIDTH = 9
const val LOWER_WIDTH = 5

const val INVALID_INPUT = -100
const val VALID_INPUT = 0

const val GAME_OVER = 0

var turn = 1
var firstPlayerScore = 0
var secondPlayerScore = 0

class GameBoard(val rows: Int = 6, val columns: Int = 7) {
    val gameBoard = Array(rows) { Array(columns) {' '} }

    fun resetBoard() {
        for (i in 0 until rows) {
            for (j in 0 until columns) {
                gameBoard[i][j] = ' '
            }
        }
    }
}

fun main() {
    println("Connect Four")
    val playerName = getPlayersName()
    val boardDimensions = getBoardDimensions()
    val rows = boardDimensions[0]
    val columns = boardDimensions[1]
    val gameBoard = GameBoard(rows, columns)


    var gamesToPlay = getNumberGames()

    var multipleGamesMode = false
    if (gamesToPlay > 1) {
        multipleGamesMode = true
    }

    println("${playerName[0]} VS ${playerName[1]}")
    println("${rows} X ${columns} board")
    if (!multipleGamesMode) {
        println("Single game")
    } else {
        println("Total $gamesToPlay games")
    }

    var playerIndex = 0
    while (gamesToPlay > 0) {
        if (multipleGamesMode) {
            println("Game #$turn")
        }
        gameBoard.resetBoard()
        val playerVictoryOrDraw = gameLoop(gameBoard, playerName, rows, columns, playerIndex)
        gamesToPlay--
        playerIndex = when (playerIndex) {
            0 -> 1
            1 -> 0
            else -> 0
        }

        if (playerVictoryOrDraw == PLAYER_1_VICTORY) {
            firstPlayerScore += 2
        } else if (playerVictoryOrDraw == PLAYER_2_VICTORY) {
            secondPlayerScore += 2
        } else if (playerVictoryOrDraw == GAME_IS_A_DRAW) {
            firstPlayerScore += 1
            secondPlayerScore += 1
        }

        if (multipleGamesMode) {
            println("Score")
            print("${playerName[0]}: $firstPlayerScore ")
            println("${playerName[1]}: ${secondPlayerScore}")
        }
        turn++
    }

    println("Game over!")
}

// boardDimension[0] refers to rows
// boardDimension[1] refers to columns
fun gameLoop(gameBoard: GameBoard, playerName: List<String>, rows: Int, columns: Int, playerIndex: Int): Char {

    var playerIndex = playerIndex
    drawBoard(rows, columns, gameBoard) // First Draw -- empty bosard
    while (true) {
        updateBoard(gameBoard, rows, columns, playerIndex, playerName[playerIndex])
        drawBoard(rows, columns, gameBoard)
        when (playerIndex) {
            0 -> playerIndex = 1
            1 -> playerIndex = 0
        }
        var playerWon = checkPlayerVictoryOrDraw(gameBoard, rows, columns)
        if (playerWon == PLAYER_1_VICTORY) {
            println("Player ${playerName[0]} won")
            return PLAYER_1_VICTORY
        } else if (playerWon == PLAYER_2_VICTORY) {
            println("Player ${playerName[1]} won")
            return PLAYER_2_VICTORY
        } else if (playerWon == GAME_IS_A_DRAW) {
            println("It is a draw")
            return GAME_IS_A_DRAW
        }
    }
}

fun getPlayersName(): List<String> {
    println("First player's name: ")
    val firstPlayerName = readln()

    println("Second player's name: ")
    val secondPlayerName = readln()

    return listOf(firstPlayerName, secondPlayerName)
}

fun getBoardDimensions(): Array<Int> {
    var input: String?
    val dimensions: Array<Int> = Array(2) {0}
    while (true) {
        println("Set the board dimensions (Rows x Columns)")
        println("Press Enter for default (6 x 7)")
        input = readLine()
        if (input == "" || input == null) {
            return arrayOf(6, 7)
        }
        input = input.filter { !it.isWhitespace() }
        if (!validFormatInput(input)) {
            println("Invalid input")
            continue
        }

        val buffer = input.split('x', ignoreCase = true)
        val r = buffer[0].toInt()
        val c = buffer[1].toInt()
        if (r < LOWER_LENGTH || r > UPPER_LENGTH) {
            println("Board rows should be from $LOWER_LENGTH to $UPPER_WIDTH")
            continue
        } else if (c < LOWER_WIDTH || c > UPPER_WIDTH) {
            println("Board columns should be from $LOWER_WIDTH to $UPPER_WIDTH")
            continue
        } else {
            dimensions[0] = r
            dimensions[1] = c
            return dimensions
        }
    }
}

fun validFormatInput(dimension: String): Boolean {
    val regex = Regex("\\d+(?i)x\\d+")
    return regex.matches(dimension)
}

fun drawBoard(rows: Int, columns: Int, playerMoves: GameBoard): Unit {
    // Print column numbers
    for (number in 1..columns) {
        print(" $number")
    }
    print('\n')

    for (i in 0 until rows) {
        for (j in 0 until columns) {
            print("|" + playerMoves.gameBoard[i][j])
        }
        print("|\n")
    }
    println("=".repeat(columns * 2 + 1))
}

fun getPlayerMove(playerName: String, columns: Int): Int {
    while (true) {
        println(playerName + "'s turn: ")
        val input = readln()
        if (input == "end") {
            println("Game over!")
            exitProcess(QUIT_GAME)
        }
        val playerMove = input.toIntOrNull()
        if (playerMove == null) {
            println("Incorrect column number")
            continue
        } else if (playerMove < 1 || playerMove > columns) {
            println("The column number is out of range (1 - $columns)")
        } else {
            return playerMove
        }
    }
}

fun updateBoard(playerMoves: GameBoard, rows: Int, columns: Int, playerIndex: Int, playerName: String) {
    var playerMove: Int?
    while (true) {
        playerMove = getPlayerMove(playerName, columns) - 1
        if (playerMoves.gameBoard[0][playerMove] != ' ') {
            println("Column ${playerMove + 1} is full")
            continue
        }
        else if (playerMoves.gameBoard[rows - 1][playerMove] == ' ') {
            playerMoves.gameBoard[rows - 1][playerMove] = PLAYER_CHARACTER[playerIndex]
            return
        } else {
            for (row in 0..rows - 2) {
                if (playerMoves.gameBoard[row + 1][playerMove] != ' ') {
                    playerMoves.gameBoard[row][playerMove] = PLAYER_CHARACTER[playerIndex]
                    return
                }
            }
        }
    }
}

fun checkPlayerVictoryOrDraw(playerMoves: GameBoard, rows: Int, columns: Int): Char {
    if (checkForDraw(playerMoves, rows, columns)) return GAME_IS_A_DRAW

    var condition = checkHorizontally(playerMoves, rows, columns)
    if (condition != GAME_NOT_OVER) return condition

    condition = checkVertically(playerMoves, rows, columns)
    if (condition != GAME_NOT_OVER) return condition

    condition = checkDiagonally(playerMoves, rows, columns)
    if (condition != GAME_NOT_OVER) return condition


    return GAME_NOT_OVER
}

fun checkForDraw(playerMoves: GameBoard, rows: Int, columns: Int): Boolean {
    for (list in playerMoves.gameBoard) {
        for (element in list) {
            if (element == ' ') {
                return false
            }
        }
    }
    return true
}

// For each player_character element at least 3 cells from edge
// checks if 3 next elements in same row match
fun checkHorizontally(playerMoves: GameBoard, rows: Int, columns: Int): Char {
    for (row in rows - 1 downTo 0) {
        label@ for (column in 0..columns - 4) {
            if (playerMoves.gameBoard[row][column] in PLAYER_CHARACTER) {
                for (i in 1..3) {
                    if (playerMoves.gameBoard[row][column + i] != playerMoves.gameBoard[row][column]) {
                        continue@label
                    }
                }
                return playerMoves.gameBoard[row][column]
            }
        }
    }
    return GAME_NOT_OVER
}

fun checkVertically(playerMoves: GameBoard, rows: Int, columns: Int): Char {
    for (row in rows - 1 downTo 3) {
        label@ for (column in 0..columns - 1) {
            if (playerMoves.gameBoard[row][column] in PLAYER_CHARACTER) {
                for (i in 1..3) {
                    if (playerMoves.gameBoard[row - i][column] != playerMoves.gameBoard[row][column]) {
                        continue@label
                    }
                }
                return playerMoves.gameBoard[row][column]
            }
        }
    }
    return GAME_NOT_OVER
}

fun checkDiagonally(playerMoves: GameBoard, rows: Int, columns: Int): Char {
    // Main diagonal
    for (row in rows - 1 downTo 3) {
        label@ for (column in columns - 1 downTo 3) {
            if (playerMoves.gameBoard[row][column] in PLAYER_CHARACTER) {
                for (i in 1..3) {
                    if (playerMoves.gameBoard[row - i][column - i] != playerMoves.gameBoard[row][column]) {
                        continue@label
                    }
                }
                return playerMoves.gameBoard[row][column]
            }
        }
    }

    // Secondary Diagonal
    for (row in rows - 1 downTo 3) {
        label@ for (column in 0..columns - 4) {
            if (playerMoves.gameBoard[row][column] in PLAYER_CHARACTER) {
                for (i in 1..3) {
                    if (playerMoves.gameBoard[row - i][column + i] != playerMoves.gameBoard[row][column]) {
                        continue@label
                    }
                }
                return playerMoves.gameBoard[row][column]
            }
        }
    }

    return GAME_NOT_OVER
}

fun getNumberGames(): Int {
    println("Do you want to play single or multiple games?")
    println("For a single game, input 1 or press Enter")
    println("Input a number of games:")
    var input = readln()
    while (input.toIntOrNull() == null || input.toInt() < 1) {
        if (input.isEmpty()) return 1
        println("Invalid Input")
        println("Do you want to play single or multiple games?")
        println("For a single game, input 1 or press Enter")
        println("Input a number of games:")
        input = readln()
    }

    if (input.isEmpty()) return 1
    else return input.toInt()
}