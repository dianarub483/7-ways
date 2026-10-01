package com.FqJvXmR.nKpTzL.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.FqJvXmR.nKpTzL.core.config.GameConfig
import com.FqJvXmR.nKpTzL.domain.model.GridPoint
import com.FqJvXmR.nKpTzL.domain.model.LevelBoard
import com.FqJvXmR.nKpTzL.domain.repository.LevelRepository
import com.FqJvXmR.nKpTzL.domain.repository.ProgressRepository
import com.FqJvXmR.nKpTzL.domain.usecase.EvaluateRoundUseCase
import com.FqJvXmR.nKpTzL.domain.usecase.GenerateLevelUseCase
import com.FqJvXmR.nKpTzL.domain.usecase.RotateCellUseCase
import com.FqJvXmR.nKpTzL.domain.usecase.SaveProgressUseCase
import com.FqJvXmR.nKpTzL.domain.usecase.TraceBeamsUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameViewModel(
    private val generateLevel: GenerateLevelUseCase,
    private val rotateCell: RotateCellUseCase,
    private val traceBeams: TraceBeamsUseCase,
    private val evaluateRound: EvaluateRoundUseCase,
    private val saveProgress: SaveProgressUseCase,
    private val progressRepository: ProgressRepository,
    private val levelRepository: LevelRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        GameUiState(
            phase = GamePhase.READY,
            levelIndex = 1,
            setName = "",
            puzzle = null,
            movesLeft = 0,
            moveLimit = 0,
            checksLeft = 0,
            linked = 0,
            totalBeams = 0,
            traces = emptyList(),
            conflicts = emptySet(),
            beamsVisible = false,
            checkToken = 0L,
            result = null
        )
    )
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var board: LevelBoard? = null
    private val undoStack = mutableListOf<GridPoint>()

    private var idleJob: Job? = null
    private var capJob: Job? = null
    private var checkJob: Job? = null

    private var mountedAt = 0L
    private var checkToken = 0L
    private var started = false

    fun startLevel(levelIndex: Int) {
        if (started) {
            return
        }
        started = true
        val generated = generateLevel(levelIndex)
        board = generated
        undoStack.clear()
        mountedAt = System.currentTimeMillis()
        checkToken = 0L
        _uiState.value = GameUiState(
            phase = GamePhase.READY,
            levelIndex = generated.spec.index,
            setName = generated.spec.setName,
            puzzle = generated.puzzle,
            movesLeft = generated.moveLimit,
            moveLimit = generated.moveLimit,
            checksLeft = generated.spec.checks,
            linked = 0,
            totalBeams = generated.puzzle.emitters.size,
            traces = emptyList(),
            conflicts = emptySet(),
            beamsVisible = false,
            checkToken = 0L,
            result = null
        )
        armIdleTimer()
        armHardCap()
    }

    fun onCellTapped(row: Int, col: Int): Boolean {
        val state = _uiState.value
        if (!state.interactive) {
            return false
        }
        val puzzle = state.puzzle ?: return false
        val rotated = rotateCell(puzzle, row, col) ?: return false
        undoStack.add(GridPoint(row, col))
        val moves = (state.movesLeft - 1).coerceAtLeast(0)
        _uiState.value = state.copy(
            phase = GamePhase.EDITING,
            puzzle = rotated,
            movesLeft = moves,
            traces = emptyList(),
            conflicts = emptySet(),
            beamsVisible = false
        )
        armIdleTimer()
        if (moves <= 0) {
            runCheck()
        }
        return true
    }

    fun undoLastMove() {
        val state = _uiState.value
        if (!state.interactive || undoStack.isEmpty()) {
            return
        }
        val puzzle = state.puzzle ?: return
        val last = undoStack.removeAt(undoStack.size - 1)
        val restored = rotateCell(puzzle, last.row, last.col) ?: return
        _uiState.value = state.copy(
            puzzle = restored,
            movesLeft = (state.movesLeft + 1).coerceAtMost(state.moveLimit),
            traces = emptyList(),
            conflicts = emptySet(),
            beamsVisible = false
        )
        armIdleTimer()
    }

    fun runCheck() {
        val state = _uiState.value
        if (state.phase == GamePhase.CHECKING || state.phase == GamePhase.FINISHED) {
            return
        }
        val puzzle = state.puzzle ?: return
        idleJob?.cancel()
        idleJob = null
        val trace = traceBeams(puzzle)
        checkToken++
        _uiState.value = state.copy(
            phase = GamePhase.CHECKING,
            traces = trace.traces,
            conflicts = trace.conflicts,
            linked = trace.linked,
            beamsVisible = true,
            checkToken = checkToken
        )
        checkJob?.cancel()
        checkJob = viewModelScope.launch {
            delay(GameConfig.CHECK_ANIMATION_MS + GameConfig.RESULT_DELAY_MS)
            if (trace.allLinked) {
                finishRound(true)
            } else {
                val current = _uiState.value
                val checks = current.checksLeft - 1
                if (checks <= 0 || current.movesLeft <= 0) {
                    _uiState.value = current.copy(checksLeft = checks.coerceAtLeast(0))
                    finishRound(false)
                } else {
                    _uiState.value = current.copy(phase = GamePhase.EDITING, checksLeft = checks)
                    armIdleTimer()
                }
            }
        }
    }

    private fun forceFinalCheck() {
        val state = _uiState.value
        if (state.phase == GamePhase.FINISHED) {
            return
        }
        val puzzle = state.puzzle ?: return
        checkJob?.cancel()
        cancelTimers()
        val trace = traceBeams(puzzle)
        checkToken++
        _uiState.value = state.copy(
            phase = GamePhase.CHECKING,
            traces = trace.traces,
            conflicts = trace.conflicts,
            linked = trace.linked,
            beamsVisible = true,
            checkToken = checkToken
        )
        checkJob = viewModelScope.launch {
            delay(GameConfig.CHECK_ANIMATION_MS + GameConfig.RESULT_DELAY_MS)
            finishRound(trace.allLinked)
        }
    }

    private fun finishRound(win: Boolean) {
        cancelTimers()
        val state = _uiState.value
        if (state.result != null) {
            return
        }
        val par = board?.par ?: 1
        val result = evaluateRound(
            state.levelIndex,
            win,
            state.movesLeft,
            state.checksLeft,
            par,
            progressRepository.bestScore(),
            levelRepository.totalLevels()
        )
        saveProgress(result)
        _uiState.value = state.copy(phase = GamePhase.FINISHED, result = result)
    }

    private fun armIdleTimer() {
        idleJob?.cancel()
        val elapsed = System.currentTimeMillis() - mountedAt
        val floor = GameConfig.IDLE_MOUNT_FLOOR_MS - elapsed
        val wait = if (floor > GameConfig.IDLE_ENGAGED_MS) floor else GameConfig.IDLE_ENGAGED_MS
        idleJob = viewModelScope.launch {
            delay(wait)
            forceFinalCheck()
        }
    }

    private fun armHardCap() {
        capJob?.cancel()
        capJob = viewModelScope.launch {
            delay(GameConfig.ROUND_HARD_CAP_MS)
            forceFinalCheck()
        }
    }

    private fun cancelTimers() {
        idleJob?.cancel()
        capJob?.cancel()
        idleJob = null
        capJob = null
    }

    override fun onCleared() {
        cancelTimers()
        checkJob?.cancel()
        checkJob = null
        super.onCleared()
    }
}
