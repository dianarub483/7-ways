package com.FqJvXmR.nKpTzL.domain.model

data class GridPoint(val row: Int, val col: Int) {
    fun key(size: Int): Int = row * size + col
}
