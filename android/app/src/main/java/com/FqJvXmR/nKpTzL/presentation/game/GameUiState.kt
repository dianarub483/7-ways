package com.FqJvXmR.nKpTzL.presentation.game

import com.FqJvXmR.nKpTzL.domain.model.BeamTrace
import com.FqJvXmR.nKpTzL.domain.model.PuzzleState
import com.FqJvXmR.nKpTzL.domain.model.RoundResult

data class GameUiState(
    val phase: GamePhase,
    val levelIndex: Int,
    val setName: String,
    val puzzle: PuzzleState?,
    val movesLeft: Int,
    val moveLimit: Int,
    val checksLeft: Int,
    val linked: Int,
    val totalBeams: Int,
    val traces: List<BeamTrace>,
    val conflicts: Set<Int>,
    val beamsVisible: Boolean,
    val checkToken: Long,
    val result: RoundResult?
) {
    val interactive: Boolean
        get() = phase == GamePhase.READY || phase == GamePhase.EDITING

    val lowOnMoves: Boolean
        get() = movesLeft <= 3
}
