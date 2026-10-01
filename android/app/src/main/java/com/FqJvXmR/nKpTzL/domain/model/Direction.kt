package com.FqJvXmR.nKpTzL.domain.model

enum class Direction(val rowStep: Int, val colStep: Int) {
    UP(-1, 0),
    DOWN(1, 0),
    LEFT(0, -1),
    RIGHT(0, 1);

    fun throughSlash(): Direction = when (this) {
        RIGHT -> UP
        UP -> RIGHT
        LEFT -> DOWN
        DOWN -> LEFT
    }

    fun throughBackslash(): Direction = when (this) {
        RIGHT -> DOWN
        DOWN -> RIGHT
        LEFT -> UP
        UP -> LEFT
    }
}
