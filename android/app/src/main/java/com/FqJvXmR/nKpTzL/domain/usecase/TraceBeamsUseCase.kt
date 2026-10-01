package com.FqJvXmR.nKpTzL.domain.usecase

import com.FqJvXmR.nKpTzL.domain.model.BeamStatus
import com.FqJvXmR.nKpTzL.domain.model.BeamTrace
import com.FqJvXmR.nKpTzL.domain.model.CellPiece
import com.FqJvXmR.nKpTzL.domain.model.Direction
import com.FqJvXmR.nKpTzL.domain.model.GridPoint
import com.FqJvXmR.nKpTzL.domain.model.PuzzleState
import com.FqJvXmR.nKpTzL.domain.model.TraceResult

class TraceBeamsUseCase {

    operator fun invoke(state: PuzzleState): TraceResult {
        val size = state.size
        val visits = HashMap<Int, MutableSet<Int>>()
        val raw = mutableListOf<BeamTrace>()

        state.emitters.forEachIndexed { index, emitter ->
            val path = mutableListOf(GridPoint(emitter.row, emitter.col))
            var row = emitter.row
            var col = emitter.col
            var dir = Direction.RIGHT
            var status = BeamStatus.DEAD
            var steps = 0
            while (steps < MAX_STEPS) {
                steps++
                row += dir.rowStep
                col += dir.colStep
                if (!state.isInside(row, col)) {
                    status = BeamStatus.DEAD
                    break
                }
                val receiver = state.receiverAt(row, col)
                if (receiver != null) {
                    path.add(GridPoint(row, col))
                    status = if (receiver.color == emitter.color) BeamStatus.LINKED else BeamStatus.WRONG
                    break
                }
                if (state.emitterAt(row, col) != null) {
                    status = BeamStatus.DEAD
                    break
                }
                val piece = state.pieceAt(row, col)
                if (piece == CellPiece.WALL) {
                    status = BeamStatus.DEAD
                    break
                }
                path.add(GridPoint(row, col))
                visits.getOrPut(row * size + col) { mutableSetOf() }.add(index)
                dir = piece.deflect(dir)
            }
            raw.add(BeamTrace(emitter.color, path.toList(), status))
        }

        val conflicts = visits.filterValues { it.size > 1 }.keys.toSet()
        val traces = raw.mapIndexed { index, trace ->
            val crossed = trace.path.any { point ->
                val key = point.row * size + point.col
                conflicts.contains(key) && visits[key]?.contains(index) == true
            }
            if (crossed) trace.copy(status = BeamStatus.CROSSED) else trace
        }
        val linked = traces.count { it.status == BeamStatus.LINKED }
        return TraceResult(
            traces = traces,
            conflicts = conflicts,
            linked = linked,
            total = state.emitters.size
        )
    }

    private companion object {
        const val MAX_STEPS = 64
    }
}
