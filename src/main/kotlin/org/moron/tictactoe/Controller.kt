package org.moron.tictactoe

import javafx.animation.*
import javafx.fxml.FXML

import javafx.geometry.Pos
import javafx.scene.control.Alert

import javafx.scene.control.Label
import javafx.scene.layout.*

import javafx.scene.paint.Color

import javafx.scene.shape.Line
import javafx.scene.shape.Rectangle

import javafx.scene.text.Font
import javafx.scene.text.Text

import javafx.util.Duration

import java.util.*
import kotlin.system.exitProcess


const val SIDE = 69.0

private fun gameOver(winner: String, isDraw: Boolean): Unit =
    Alert(Alert.AlertType.INFORMATION).let { alert ->
        alert.title = "Game Over!"
        alert.headerText = "Game Is Over"
        if (isDraw)
            alert.contentText = "Well played the both of you!\nBut, it is a draw."
        else
            alert.contentText = "Well played the both of you!\nThe winner is $winner"
        alert.show()
    }

class Controller {
    @FXML
    lateinit var rootVBox: VBox

    @FXML
    lateinit var gamePane: AnchorPane

    @FXML
    lateinit var turnText: Label

    @FXML
    private fun quitBtnAction(): Nothing =
        exitProcess(0)

    @FXML
    private fun resetBtnAction() {
        repeat (3) { i ->
            repeat (3) { j ->
                gameBoard[j][i].setValue(null)
                gameBoard[j][i].resetAccessibleText()
            }
        }
        playable = true
        turnX = true
        turnText.text = "\"X Turn\""
        gamePane.children.remove(line)
    }

    private var line: Line = Line()

    private var playable = true

    private var turnX = true

    private val combosPossible = ArrayList<Controller.Combo>()

    private val gameBoard: Array<Array<Tile>> = Array(3) { Array(3) { Tile() } }

    fun createEmptyBoard() {
        repeat(3) { i ->
            repeat(3) { j ->
                // adding tiles to gamePane && gameBoard
                val tile = Tile().apply { translateX = j * SIDE; translateY = i * SIDE }

                gamePane.children.add(tile)

                gameBoard[j][i] = tile
            }
        }

        // horizontal
        repeat(3) { y ->
            combosPossible.add(Combo(gameBoard[0][y], gameBoard[1][y], gameBoard[2][y]))
        }

        // vertical
        repeat(3) { x ->
            combosPossible.add(Combo(gameBoard[x][0], gameBoard[x][1], gameBoard[x][2]))
        }

        // diagonal
        combosPossible.add(Combo(gameBoard[0][0], gameBoard[1][1], gameBoard[2][2]))
        combosPossible.add(Combo(gameBoard[2][0], gameBoard[1][1], gameBoard[0][2]))
    }

    private fun checkState() {
        fun Text.isAccessibleTextNotNull(): Boolean = (this.accessibleText != null)

        val completedComboFlag = Combo::isComplete
        val isGameDraw = gamePane.children.all { node ->
            node is Tile && node.children.filterIsInstance<Text>().all(Text::isAccessibleTextNotNull)
        }

        if (combosPossible.any(completedComboFlag)) {
            val winner = if (turnX) "O" else "X"

            playable = false
            turnText.text = "Game Over!"
            playWinAnimation(combosPossible.find(completedComboFlag))

            gameOver(winner, false)
            return
        }

        if (isGameDraw) {
            playable = false
            turnText.text = "Game Over!"
            gameOver("draw", true)
            return
        }
    }

    private fun playWinAnimation(combo: Combo?) {
        if (combo == null) return

        line = Line().apply {
            stroke = Color.DARKTURQUOISE
            strokeWidth = 7.0

            combo.tiles[0].center.let { (centerX, centerY) ->
                startX = centerX
                startY = centerY
                endX = centerX
                endY = centerY
            }
        }

        gamePane.children.add(line)

        Timeline().let { timeline ->
            timeline.keyFrames.add(
                KeyFrame(Duration.millis(500.0),
                    KeyValue(line.endXProperty(), combo.tiles[2].center.first),
                    KeyValue(line.endYProperty(), combo.tiles[2].center.second)
                )
            )
            timeline.play()
        }
    }

    inner class Tile : StackPane() {
        private val text = Text("")
        val center: Pair<Double, Double>
            get() = translateX+(SIDE/2) to translateY+(SIDE/2)

        private fun isDrawn(): Boolean =
            children[1].accessibleText != null

        fun getValue(): String = text.text
        fun setValue(textValue: String?) {
            text.text = textValue
        }
        fun resetAccessibleText() {
            text.accessibleText = null
        }

        init {
            val border = Rectangle(SIDE, SIDE).apply {
                fill = Color.TRANSPARENT; stroke = Color.CHOCOLATE
                strokeWidth = 3.0
            }
            alignment = Pos.CENTER
            children.addAll(border, text)

            text.font = Font.font(50.0)

            setOnMouseClicked {
                if (!playable || isDrawn())
                    return@setOnMouseClicked

                if (turnX) {
                    drawX()
                    turnX = false
                    turnText.text = "\"O Turn\""
                } else {
                    drawO()
                    turnX = true
                    turnText.text = "\"X Turn\""
                }

                checkState()
            }
        }

        private fun drawX() {
            text.text = "X"
            text.accessibleText = "X"
            text.fill = Color.MEDIUMVIOLETRED
        }

        private fun drawO() {
            text.text = "O"
            text.accessibleText = "O"
            text.fill = Color.FUCHSIA
        }
    }

    inner class Combo(
        vararg val tiles: Tile
    ) {
        fun isComplete(): Boolean {
            if (tiles[0].getValue().isEmpty())
                return false
            return tiles[0].getValue() == tiles[1].getValue() && tiles[0].getValue() == tiles[2].getValue()
        }
    }
}