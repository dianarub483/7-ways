package com.FqJvXmR.nKpTzL.presentation.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.FqJvXmR.nKpTzL.MainActivity
import com.FqJvXmR.nKpTzL.R
import com.FqJvXmR.nKpTzL.core.di.AppViewModelFactory
import com.FqJvXmR.nKpTzL.databinding.FragmentGameBinding
import kotlinx.coroutines.launch

class GameFragment : Fragment() {

    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding

    private var lastCheckToken = 0L
    private var resultDelivered = false

    private val viewModel: GameViewModel by viewModels { AppViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val inflated = FragmentGameBinding.inflate(inflater, container, false)
        _binding = inflated
        return inflated.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val views = binding ?: return

        views.gameBoard.onCellTapped = { row, col ->
            val rotated = viewModel.onCellTapped(row, col)
            if (rotated) {
                views.gameBoard.playRotation(row, col)
                pulseChip()
            }
        }
        views.gameAction.setOnClickListener {
            viewModel.runCheck()
        }
        views.gameUndo.setOnClickListener {
            viewModel.undoLastMove()
        }
        views.gameMenu.setOnClickListener {
            val host = activity as? MainActivity ?: return@setOnClickListener
            host.navigator.backToMenu()
        }
        views.gameBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        viewModel.startLevel(arguments?.getInt(ARG_LEVEL, 1) ?: 1)
        observeState()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    render(state)
                }
            }
        }
    }

    private fun render(state: GameUiState) {
        val views = binding ?: return
        val context = context ?: return
        val puzzle = state.puzzle
        if (puzzle != null) {
            views.gameBoard.setPuzzle(puzzle)
        }
        views.gameLevelTitle.text = getString(
            R.string.game_level_fmt,
            state.levelIndex,
            state.setName
        )
        views.gameSetName.text = state.setName
        views.gameMovesChip.text = getString(R.string.game_chip_fmt, state.movesLeft)
        views.gameMovesChip.setTextColor(
            ContextCompat.getColor(
                context,
                if (state.lowOnMoves) R.color.red_pulse else R.color.gold_core
            )
        )
        views.statMoves.setValueText(state.movesLeft.toString())
        views.statChecks.setValueText(state.checksLeft.toString())
        views.statLinked.setValueText(
            getString(R.string.count_fmt, state.linked, state.totalBeams)
        )
        ViewCompat.setStateDescription(
            views.gameBoard,
            getString(R.string.game_state_fmt, state.linked, state.totalBeams)
        )
        views.gameAction.isEnabled = state.interactive
        views.gameAction.alpha = if (state.interactive) 1f else DISABLED_ALPHA
        views.gameUndo.isEnabled = state.interactive
        views.gameUndo.alpha = if (state.interactive) 1f else DISABLED_ALPHA

        if (state.checkToken != lastCheckToken && state.beamsVisible) {
            lastCheckToken = state.checkToken
            views.gameBoard.setTraces(state.traces, state.conflicts, true)
            views.gameBoard.playReveal {
                try {
                    val current = binding ?: return@playReveal
                    current.gameBoard.invalidate()
                } catch (e: Exception) {
                    lastCheckToken = state.checkToken
                }
            }
        } else {
            views.gameBoard.setTraces(state.traces, state.conflicts, state.beamsVisible)
        }

        val result = state.result
        if (result != null && !resultDelivered) {
            resultDelivered = true
            val host = activity as? MainActivity ?: return
            host.navigator.showResult(result)
        }
    }

    private fun pulseChip() {
        val views = binding ?: return
        views.gameMovesChip.animate().cancel()
        views.gameMovesChip.scaleX = CHIP_PULSE
        views.gameMovesChip.scaleY = CHIP_PULSE
        views.gameMovesChip.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(CHIP_PULSE_MS)
            .start()
    }

    override fun onDestroyView() {
        binding?.gameBoard?.onCellTapped = null
        binding?.gameBoard?.release()
        binding?.gameMovesChip?.animate()?.cancel()
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val ARG_LEVEL = "level_index"
        private const val DISABLED_ALPHA = 0.45f
        private const val CHIP_PULSE = 1.12f
        private const val CHIP_PULSE_MS = 180L

        fun newInstance(levelIndex: Int): GameFragment {
            val fragment = GameFragment()
            val args = Bundle()
            args.putInt(ARG_LEVEL, levelIndex)
            fragment.arguments = args
            return fragment
        }
    }
}
