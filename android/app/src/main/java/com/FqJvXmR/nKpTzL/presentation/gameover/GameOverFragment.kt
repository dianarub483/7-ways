package com.FqJvXmR.nKpTzL.presentation.gameover

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
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
import com.FqJvXmR.nKpTzL.databinding.FragmentGameOverBinding
import com.FqJvXmR.nKpTzL.domain.model.RoundResult
import com.FqJvXmR.nKpTzL.presentation.dialog.LevelMapDialog
import kotlinx.coroutines.launch

class GameOverFragment : Fragment() {

    private var _binding: FragmentGameOverBinding? = null
    private val binding get() = _binding

    private var flashAnimator: ObjectAnimator? = null

    private val viewModel: GameOverViewModel by viewModels { AppViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val inflated = FragmentGameOverBinding.inflate(inflater, container, false)
        _binding = inflated
        return inflated.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val views = binding ?: return
        val args = arguments ?: Bundle()
        viewModel.bind(
            win = args.getBoolean(ARG_WIN, false),
            levelIndex = args.getInt(ARG_LEVEL, 1),
            nextLevel = args.getInt(ARG_NEXT, 1),
            score = args.getInt(ARG_SCORE, 0),
            stars = args.getInt(ARG_STARS, 0),
            movesLeft = args.getInt(ARG_MOVES, 0),
            checksLeft = args.getInt(ARG_CHECKS, 0),
            newBest = args.getBoolean(ARG_BEST, false)
        )

        views.resultReplay.setOnClickListener {
            val host = activity as? MainActivity ?: return@setOnClickListener
            val state = viewModel.uiState.value ?: return@setOnClickListener
            host.navigator.restartGame(if (state.win) state.nextLevel else state.levelIndex)
        }
        views.resultMap.setOnClickListener {
            LevelMapDialog().show(parentFragmentManager, TAG_MAP)
        }
        views.resultMenu.setOnClickListener {
            val host = activity as? MainActivity ?: return@setOnClickListener
            host.navigator.backToMenu()
        }

        playCardEntrance(views)
        observeState()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    if (state != null) {
                        render(state)
                    }
                }
            }
        }
    }

    private fun render(state: GameOverUiState) {
        val views = binding ?: return
        val context = context ?: return

        views.resultOverlay.setBackgroundResource(
            if (state.win) R.drawable.gradient_win else R.drawable.gradient_lose
        )
        views.resultTitle.text = getString(if (state.win) R.string.result_win else R.string.result_lose)
        views.resultTitle.setTextColor(
            ContextCompat.getColor(context, if (state.win) R.color.gold_core else R.color.red_pulse)
        )
        views.resultSubtitle.text =
            getString(if (state.win) R.string.result_win_sub else R.string.result_lose_sub)
        views.resultEmblem.visibility = if (state.win) View.VISIBLE else View.GONE
        views.resultBurst.visibility = if (state.win) View.GONE else View.VISIBLE
        views.resultBestBadge.visibility = if (state.newBest) View.VISIBLE else View.GONE

        views.resultNextHint.text = if (state.win && !state.isLastLevel) {
            getString(R.string.result_next_fmt, state.nextLevel)
        } else {
            getString(R.string.result_retry_fmt, state.levelIndex)
        }

        val visibleStats = listOf(state.score > 0, state.movesLeft > 0, state.checksLeft > 0)
            .count { it }
        if (visibleStats < 2) {
            views.resultStatsRow.visibility = View.GONE
        } else {
            views.resultStatsRow.visibility = View.VISIBLE
            views.resultStatScore.visibility = if (state.score > 0) View.VISIBLE else View.INVISIBLE
            views.resultStatMoves.visibility = if (state.movesLeft > 0) View.VISIBLE else View.INVISIBLE
            views.resultStatChecks.visibility = if (state.checksLeft > 0) View.VISIBLE else View.INVISIBLE
            views.resultStatScore.bind(getString(R.string.stat_score), state.score.toString())
            views.resultStatMoves.bind(getString(R.string.stat_moves_left), state.movesLeft.toString())
            views.resultStatChecks.bind(getString(R.string.stat_checks), state.checksLeft.toString())
        }

        ViewCompat.setStateDescription(views.resultTitle, views.resultSubtitle.text)

        if (!state.win) {
            playOverloadFlash(views.resultFlash)
        }
    }

    private fun playCardEntrance(views: FragmentGameOverBinding) {
        views.resultCard.alpha = 0f
        views.resultCard.scaleX = CARD_START_SCALE
        views.resultCard.scaleY = CARD_START_SCALE
        views.resultCard.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(CARD_IN_MS)
            .setInterpolator(DecelerateInterpolator())
            .start()
    }

    private fun playOverloadFlash(target: View) {
        flashAnimator?.cancel()
        val animator = ObjectAnimator.ofFloat(target, View.ALPHA, 0f, FLASH_ALPHA)
        animator.duration = FLASH_MS
        animator.repeatCount = FLASH_REPEATS
        animator.repeatMode = ValueAnimator.REVERSE
        animator.start()
        flashAnimator = animator
    }

    override fun onDestroyView() {
        flashAnimator?.cancel()
        flashAnimator = null
        binding?.resultCard?.animate()?.cancel()
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val TAG_MAP = "level_map"
        private const val ARG_WIN = "result_win"
        private const val ARG_LEVEL = "result_level"
        private const val ARG_NEXT = "result_next"
        private const val ARG_SCORE = "result_score"
        private const val ARG_STARS = "result_stars"
        private const val ARG_MOVES = "result_moves"
        private const val ARG_CHECKS = "result_checks"
        private const val ARG_BEST = "result_best"
        private const val CARD_START_SCALE = 0.9f
        private const val CARD_IN_MS = 380L
        private const val FLASH_ALPHA = 0.35f
        private const val FLASH_MS = 160L
        private const val FLASH_REPEATS = 3

        fun newInstance(result: RoundResult): GameOverFragment {
            val fragment = GameOverFragment()
            val args = Bundle()
            args.putBoolean(ARG_WIN, result.win)
            args.putInt(ARG_LEVEL, result.levelIndex)
            args.putInt(ARG_NEXT, result.nextLevel)
            args.putInt(ARG_SCORE, result.score)
            args.putInt(ARG_STARS, result.stars)
            args.putInt(ARG_MOVES, result.movesLeft)
            args.putInt(ARG_CHECKS, result.checksLeft)
            args.putBoolean(ARG_BEST, result.newBest)
            fragment.arguments = args
            return fragment
        }
    }
}
