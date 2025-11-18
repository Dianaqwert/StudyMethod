package com.example.proyecto_final

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

class SnakeGameView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private val snake = mutableListOf(Pair(5, 5))
    private var direction = Direction.RIGHT
    private var food = Pair(10, 10)

    private val paintSnake = Paint().apply { color = Color.BLUE }
    private val paintFood = Paint().apply { color = Color.RED }
    private val paintGrid = Paint().apply { color = Color.rgb(210, 240, 210) }

    private val cellSize = 55
    private var lastX = 0f
    private var lastY = 0f

    private var gameLoop: Runnable? = null

    // dimensiones reales del grid
    private var gridWidth = 0
    private var gridHeight = 0

    enum class Direction { UP, DOWN, LEFT, RIGHT }

    init {
        startGame()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        // Calcular grid real
        gridWidth = w / cellSize
        gridHeight = h / cellSize

        // Colocar comida dentro del grid correcto
        food = generateFood()
    }

    private fun generateFood(): Pair<Int, Int> {
        return Pair((0 until gridWidth).random(), (0 until gridHeight).random())
    }

    private fun startGame() {
        gameLoop = object : Runnable {
            override fun run() {
                moveSnake()
                invalidate()
                postDelayed(this, 150)
            }
        }
        post(gameLoop!!)
    }

    private fun moveSnake() {

        if (gridWidth == 0 || gridHeight == 0) return  // evita errores antes de medir

        val head = snake.first()

        val newHead = when (direction) {
            Direction.UP -> Pair(head.first, head.second - 1)
            Direction.DOWN -> Pair(head.first, head.second + 1)
            Direction.LEFT -> Pair(head.first - 1, head.second)
            Direction.RIGHT -> Pair(head.first + 1, head.second)
        }

        // perder
        if (newHead.first < 0 || newHead.second < 0 ||
            newHead.first >= gridWidth || newHead.second >= gridHeight ||
            snake.contains(newHead)
        ) {
            restartGame()
            return
        }

        snake.add(0, newHead)

        // comer
        if (newHead == food) {
            food = generateFood()
        } else {
            snake.removeAt(snake.size - 1)
        }
    }

    private fun restartGame() {
        snake.clear()
        snake.add(Pair(5, 5))
        direction = Direction.RIGHT

        if (gridWidth > 0 && gridHeight > 0) {
            food = generateFood()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // cuadricula
        for (i in 0..gridWidth) {
            for (j in 0..gridHeight) {
                canvas.drawRect(
                    (i * cellSize).toFloat(),
                    (j * cellSize).toFloat(),
                    ((i + 1) * cellSize).toFloat(),
                    ((j + 1) * cellSize).toFloat(),
                    paintGrid
                )
            }
        }

        // serpiente
        snake.forEach {
            canvas.drawRect(
                (it.first * cellSize).toFloat(),
                (it.second * cellSize).toFloat(),
                ((it.first + 1) * cellSize).toFloat(),
                ((it.second + 1) * cellSize).toFloat(),
                paintSnake
            )
        }

        // comida
        canvas.drawRect(
            (food.first * cellSize).toFloat(),
            (food.second * cellSize).toFloat(),
            ((food.first + 1) * cellSize).toFloat(),
            ((food.second + 1) * cellSize).toFloat(),
            paintFood
        )
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = event.x
                lastY = event.y
            }
            MotionEvent.ACTION_UP -> {
                val dx = event.x - lastX
                val dy = event.y - lastY

                direction =
                    if (abs(dx) > abs(dy)) {
                        if (dx > 0) Direction.RIGHT else Direction.LEFT
                    } else {
                        if (dy > 0) Direction.DOWN else Direction.UP
                    }
            }
        }
        return true
    }
}
