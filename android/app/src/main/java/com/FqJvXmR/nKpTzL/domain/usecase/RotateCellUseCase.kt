package com.FqJvXmR.nKpTzL.domain.usecase

import com.FqJvXmR.nKpTzL.domain.model.PuzzleState

class RotateCellUseCase {

    operator fun invoke(state: PuzzleState, row: Int, col: Int): PuzzleState? {
        if (!state.isInside(row, col) || state.isTerminal(row, col)) {
            return null
        }
        val piece = state.pieceAt(row, col)
        if (!piece.isPrism) {
            return null
        }
        return state.withPiece(row, col, piece.flipped())
    }
}
