package com.FqJvXmR.nKpTzL.domain.usecase

import com.FqJvXmR.nKpTzL.domain.model.BeamColor
import com.FqJvXmR.nKpTzL.domain.model.CellPiece
import com.FqJvXmR.nKpTzL.domain.model.Direction
import com.FqJvXmR.nKpTzL.domain.model.GridPoint
import com.FqJvXmR.nKpTzL.domain.model.LevelBoard
import com.FqJvXmR.nKpTzL.domain.model.LevelSpec
import com.FqJvXmR.nKpTzL.domain.model.PuzzleState
import com.FqJvXmR.nKpTzL.domain.model.Terminal
import com.FqJvXmR.nKpTzL.domain.repository.LevelRepository
import kotlin.random.Random

class GenerateLevelUseCase(private val levelRepository: LevelRepository) {

    operator fun invoke(levelIndex: Int): LevelBoard {
        val spec = levelRepository.specFor(levelIndex)
        var attempt = 0
        while (attempt < MAX_ATTEMPTS) {
            val board = build(spec, Random(spec.seed + attempt * 977L))
            if (board != null) {
                return board
            }
            attempt++
        }
        return fallback(spec)
    }

    private fun build(spec: LevelSpec, random: Random): LevelBoard? {
        val size = BOARD_SIZE
        val beams = spec.beamCount.coerceIn(2, size)
        val emitterRows = (0 until size).shuffled(random).take(beams).sorted()
        val receiverRows = (0 until size).shuffled(random).take(beams).sorted()
        val palette = BeamColor.values().toList().shuffled(random).take(beams)

        val pieces = Array(size) { Array(size) { CellPiece.EMPTY } }
        val blocked = Array(size) { BooleanArray(size) }
        for (row in 0 until size) {
            blocked[row][0] = true
            blocked[row][size - 1] = true
        }

        val solutionPrisms = mutableListOf<GridPoint>()
        for (beam in 0 until beams) {
            val path = mutableListOf<GridPoint>()
            val budget = intArrayOf(NODE_BUDGET)
            val startRow = emitterRows[beam]
            val solved = route(
                pieces = pieces,
                blocked = blocked,
                size = size,
                row = startRow,
                col = 1,
                dir = Direction.RIGHT,
                targetRow = receiverRows[beam],
                depth = 0,
                random = random,
                budget = budget,
                path = path
            )
            if (!solved) {
                return null
            }
            for (point in path) {
                if (pieces[point.row][point.col].isPrism) {
                    solutionPrisms.add(point)
                }
            }
        }

        if (solutionPrisms.isEmpty()) {
            return null
        }

        for (row in 0 until size) {
            for (col in 1 until size - 1) {
                if (blocked[row][col]) {
                    continue
                }
                val roll = random.nextInt(100)
                pieces[row][col] = when {
                    roll < WALL_CHANCE && col > 1 -> CellPiece.WALL
                    roll < WALL_CHANCE + DECOY_CHANCE ->
                        if (random.nextBoolean()) CellPiece.PRISM_SLASH else CellPiece.PRISM_BACKSLASH
                    else -> CellPiece.EMPTY
                }
            }
        }

        val flipTargets = solutionPrisms.shuffled(random).take(spec.scramble)
        for (point in flipTargets) {
            pieces[point.row][point.col] = pieces[point.row][point.col].flipped()
        }
        val par = flipTargets.size
        if (par == 0) {
            return null
        }

        val emitters = emitterRows.mapIndexed { index, row -> Terminal(row, 0, palette[index]) }
        val receivers = receiverRows.mapIndexed { index, row -> Terminal(row, size - 1, palette[index]) }
        val grid = pieces.map { line -> line.toList() }
        return LevelBoard(
            spec = spec,
            puzzle = PuzzleState(size, grid, emitters, receivers),
            par = par,
            moveLimit = par + MOVE_SLACK
        )
    }

    private fun route(
        pieces: Array<Array<CellPiece>>,
        blocked: Array<BooleanArray>,
        size: Int,
        row: Int,
        col: Int,
        dir: Direction,
        targetRow: Int,
        depth: Int,
        random: Random,
        budget: IntArray,
        path: MutableList<GridPoint>
    ): Boolean {
        if (budget[0] <= 0) {
            return false
        }
        budget[0] = budget[0] - 1
        if (row < 0 || row >= size || col < 0 || col >= size) {
            return false
        }
        if (col == size - 1) {
            return row == targetRow
        }
        if (blocked[row][col] || depth > MAX_DEPTH) {
            return false
        }
        blocked[row][col] = true
        val options = mutableListOf(CellPiece.EMPTY, CellPiece.PRISM_SLASH, CellPiece.PRISM_BACKSLASH)
        options.shuffle(random)
        if (random.nextInt(100) < STRAIGHT_BIAS) {
            options.remove(CellPiece.EMPTY)
            options.add(0, CellPiece.EMPTY)
        }
        for (piece in options) {
            val out = piece.deflect(dir)
            val nextRow = row + out.rowStep
            val nextCol = col + out.colStep
            val reached = route(
                pieces, blocked, size, nextRow, nextCol, out,
                targetRow, depth + 1, random, budget, path
            )
            if (reached) {
                pieces[row][col] = piece
                path.add(GridPoint(row, col))
                return true
            }
        }
        blocked[row][col] = false
        return false
    }

    private fun fallback(spec: LevelSpec): LevelBoard {
        val size = BOARD_SIZE
        val pieces = Array(size) { Array(size) { CellPiece.EMPTY } }
        pieces[3][2] = CellPiece.PRISM_BACKSLASH
        pieces[4][2] = CellPiece.PRISM_BACKSLASH
        pieces[4][3] = CellPiece.PRISM_SLASH
        pieces[3][3] = CellPiece.PRISM_SLASH
        pieces[1][4] = CellPiece.WALL
        pieces[5][2] = CellPiece.WALL
        pieces[3][2] = pieces[3][2].flipped()
        pieces[4][3] = pieces[4][3].flipped()
        val rows = listOf(0, 3, 6)
        val palette = listOf(BeamColor.GOLD, BeamColor.BLUE, BeamColor.RED)
        val emitters = rows.mapIndexed { index, row -> Terminal(row, 0, palette[index]) }
        val receivers = rows.mapIndexed { index, row -> Terminal(row, size - 1, palette[index]) }
        val grid = pieces.map { line -> line.toList() }
        return LevelBoard(
            spec = spec,
            puzzle = PuzzleState(size, grid, emitters, receivers),
            par = 2,
            moveLimit = 10
        )
    }

    private companion object {
        const val BOARD_SIZE = 7
        const val MAX_ATTEMPTS = 200
        const val MAX_DEPTH = 10
        const val NODE_BUDGET = 40000
        const val WALL_CHANCE = 22
        const val DECOY_CHANCE = 30
        const val STRAIGHT_BIAS = 55
        const val MOVE_SLACK = 6
    }
}
