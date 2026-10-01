package com.FqJvXmR.nKpTzL.domain.model

data class PuzzleState(
    val size: Int,
    val grid: List<List<CellPiece>>,
    val emitters: List<Terminal>,
    val receivers: List<Terminal>
) {
    fun isInside(row: Int, col: Int): Boolean =
        row >= 0 && row < size && col >= 0 && col < size

    fun pieceAt(row: Int, col: Int): CellPiece =
        if (isInside(row, col)) grid[row][col] else CellPiece.WALL

    fun emitterAt(row: Int, col: Int): Terminal? =
        emitters.firstOrNull { it.row == row && it.col == col }

    fun receiverAt(row: Int, col: Int): Terminal? =
        receivers.firstOrNull { it.row == row && it.col == col }

    fun isTerminal(row: Int, col: Int): Boolean =
        emitterAt(row, col) != null || receiverAt(row, col) != null

    fun withPiece(row: Int, col: Int, piece: CellPiece): PuzzleState {
        val next = grid.mapIndexed { r, line ->
            if (r != row) line else line.mapIndexed { c, cell -> if (c == col) piece else cell }
        }
        return copy(grid = next)
    }
}
