package com.FqJvXmR.nKpTzL.core.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import com.FqJvXmR.nKpTzL.core.config.GameConfig
import com.FqJvXmR.nKpTzL.domain.model.BeamStatus
import com.FqJvXmR.nKpTzL.domain.model.BeamTrace
import com.FqJvXmR.nKpTzL.domain.model.CellPiece
import com.FqJvXmR.nKpTzL.domain.model.PuzzleState
import kotlin.math.floor
import kotlin.math.min

class BeamBoardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var onCellTapped: ((Int, Int) -> Unit)? = null

    private val density = resources.displayMetrics.density
    private val framePx = (GameConfig.BOARD_PAD_DP + GameConfig.BOARD_STROKE_DP) * density
    private val maxBoardPx = GameConfig.BOARD_MAX_DP * density

    private var tile = 0f
    private var boardSide = 0f

    private var puzzle: PuzzleState? = null
    private var traces: List<BeamTrace> = emptyList()
    private var conflicts: Set<Int> = emptySet()
    private var beamsVisible = false
    private var reveal = 1f

    private var rotatingRow = -1
    private var rotatingCol = -1
    private var rotateProgress = 0f

    private var revealAnimator: ValueAnimator? = null
    private var rotateAnimator: ValueAnimator? = null

    private val cellPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = 0xFF1A1D2B.toInt()
    }
    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = density
        color = 0x1AFFF0D2
    }
    private val wallPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = 0xFF232739.toInt()
    }
    private val wallEdgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = density
        color = 0x33FFF0D2
    }
    private val prismPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        color = 0xFFF2C54C.toInt()
    }
    private val prismGlossPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        color = 0x66FFFFFF
    }
    private val terminalPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }
    private val beamPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }
    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }
    private val conflictPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = 0x66E33B45
    }

    private val cellRect = RectF()
    private val beamPath = Path()
    private val segmentPath = Path()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val widthAvailable = if (widthMode == MeasureSpec.UNSPECIFIED) {
            maxBoardPx
        } else {
            MeasureSpec.getSize(widthMeasureSpec).toFloat()
        }
        val heightAvailable = if (heightMode == MeasureSpec.UNSPECIFIED) {
            maxBoardPx
        } else {
            MeasureSpec.getSize(heightMeasureSpec).toFloat()
        }
        val side = min(min(widthAvailable, heightAvailable), maxBoardPx)
        val usable = side - 2f * framePx
        val cells = GameConfig.BOARD_SIZE
        tile = floor(usable / cells).coerceAtLeast(1f)
        boardSide = tile * cells + 2f * framePx
        val measured = boardSide.toInt()
        setMeasuredDimension(measured, measured)
    }

    fun setPuzzle(state: PuzzleState) {
        puzzle = state
        invalidate()
    }

    fun setTraces(list: List<BeamTrace>, conflictKeys: Set<Int>, visible: Boolean) {
        traces = list
        conflicts = conflictKeys
        beamsVisible = visible
        invalidate()
    }

    fun playReveal(onDone: () -> Unit) {
        revealAnimator?.cancel()
        reveal = 0f
        beamsVisible = true
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = GameConfig.CHECK_ANIMATION_MS
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.addUpdateListener { value ->
            reveal = value.animatedValue as Float
            invalidate()
        }
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                try {
                    reveal = 1f
                    invalidate()
                    onDone()
                } catch (e: Exception) {
                    reveal = 1f
                }
            }
        })
        revealAnimator = animator
        animator.start()
    }

    fun playRotation(row: Int, col: Int) {
        rotateAnimator?.cancel()
        rotatingRow = row
        rotatingCol = col
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = ROTATE_MS
        animator.interpolator = OvershootInterpolator(1.1f)
        animator.addUpdateListener { value ->
            rotateProgress = value.animatedValue as Float
            invalidate()
        }
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                try {
                    rotatingRow = -1
                    rotatingCol = -1
                    rotateProgress = 0f
                    invalidate()
                } catch (e: Exception) {
                    rotatingRow = -1
                }
            }
        })
        rotateAnimator = animator
        animator.start()
    }

    fun release() {
        revealAnimator?.cancel()
        rotateAnimator?.cancel()
        revealAnimator = null
        rotateAnimator = null
    }

    override fun onDetachedFromWindow() {
        release()
        super.onDetachedFromWindow()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            return true
        }
        if (event.action == MotionEvent.ACTION_UP) {
            val state = puzzle
            if (state != null && tile > 0f) {
                val col = floor((event.x - framePx) / tile).toInt()
                val row = floor((event.y - framePx) / tile).toInt()
                if (state.isInside(row, col)) {
                    performClick()
                    onCellTapped?.invoke(row, col)
                }
            }
            return true
        }
        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val state = puzzle ?: return
        val size = state.size
        val radius = tile * 0.14f
        for (row in 0 until size) {
            for (col in 0 until size) {
                val left = framePx + col * tile
                val top = framePx + row * tile
                cellRect.set(left + 1f, top + 1f, left + tile - 1f, top + tile - 1f)
                canvas.drawRoundRect(cellRect, radius, radius, cellPaint)
                canvas.drawRoundRect(cellRect, radius, radius, gridPaint)
            }
        }

        for (row in 0 until size) {
            for (col in 0 until size) {
                if (state.isTerminal(row, col)) {
                    continue
                }
                val piece = state.pieceAt(row, col)
                when {
                    piece == CellPiece.WALL -> drawWall(canvas, row, col, radius)
                    piece.isPrism -> drawPrism(canvas, row, col, piece)
                    else -> Unit
                }
            }
        }

        state.emitters.forEach { terminal ->
            val cx = framePx + (terminal.col + 0.5f) * tile
            val cy = framePx + (terminal.row + 0.5f) * tile
            ringPaint.strokeWidth = 2f * density
            ringPaint.color = withAlpha(terminal.color.argb, 0x55)
            canvas.drawCircle(cx, cy, tile * 0.38f, ringPaint)
            terminalPaint.color = terminal.color.argb
            canvas.drawCircle(cx, cy, tile * 0.26f, terminalPaint)
        }

        state.receivers.forEachIndexed { index, terminal ->
            val cx = framePx + (terminal.col + 0.5f) * tile
            val cy = framePx + (terminal.row + 0.5f) * tile
            val linked = beamsVisible &&
                traces.getOrNull(index)?.status == BeamStatus.LINKED
            ringPaint.strokeWidth = 3f * density
            ringPaint.color = terminal.color.argb
            canvas.drawCircle(cx, cy, tile * 0.32f, ringPaint)
            if (linked) {
                terminalPaint.color = withAlpha(terminal.color.argb, 0xCC)
                canvas.drawCircle(cx, cy, tile * 0.22f * reveal, terminalPaint)
            }
        }

        if (beamsVisible) {
            drawBeams(canvas)
            drawConflicts(canvas, radius, size)
        }
    }

    private fun drawWall(canvas: Canvas, row: Int, col: Int, radius: Float) {
        val left = framePx + col * tile
        val top = framePx + row * tile
        val inset = tile * 0.14f
        cellRect.set(left + inset, top + inset, left + tile - inset, top + tile - inset)
        canvas.drawRoundRect(cellRect, radius, radius, wallPaint)
        canvas.drawRoundRect(cellRect, radius, radius, wallEdgePaint)
    }

    private fun drawPrism(canvas: Canvas, row: Int, col: Int, piece: CellPiece) {
        val cx = framePx + (col + 0.5f) * tile
        val cy = framePx + (row + 0.5f) * tile
        val reach = tile * 0.30f
        val slash = piece == CellPiece.PRISM_SLASH
        val animating = row == rotatingRow && col == rotatingCol
        canvas.save()
        if (animating) {
            canvas.rotate(rotateProgress * 90f, cx, cy)
            val scale = 1f + 0.06f * (1f - rotateProgress)
            canvas.scale(scale, scale, cx, cy)
        }
        prismPaint.strokeWidth = tile * 0.22f
        prismGlossPaint.strokeWidth = tile * 0.06f
        if (slash) {
            canvas.drawLine(cx - reach, cy + reach, cx + reach, cy - reach, prismPaint)
            canvas.drawLine(cx - reach * 0.6f, cy + reach * 0.4f, cx + reach * 0.6f, cy - reach * 0.8f, prismGlossPaint)
        } else {
            canvas.drawLine(cx - reach, cy - reach, cx + reach, cy + reach, prismPaint)
            canvas.drawLine(cx - reach * 0.6f, cy - reach * 0.8f, cx + reach * 0.6f, cy + reach * 0.4f, prismGlossPaint)
        }
        canvas.restore()
    }

    private fun drawBeams(canvas: Canvas) {
        traces.forEach { trace ->
            if (trace.path.size < 2) {
                return@forEach
            }
            beamPath.reset()
            trace.path.forEachIndexed { index, point ->
                val x = framePx + (point.col + 0.5f) * tile
                val y = framePx + (point.row + 0.5f) * tile
                if (index == 0) {
                    beamPath.moveTo(x, y)
                } else {
                    beamPath.lineTo(x, y)
                }
            }
            val target: Path
            if (reveal >= 1f) {
                target = beamPath
            } else {
                segmentPath.reset()
                val measure = PathMeasure(beamPath, false)
                measure.getSegment(0f, measure.length * reveal, segmentPath, true)
                target = segmentPath
            }
            glowPaint.strokeWidth = 10f * density
            glowPaint.color = withAlpha(trace.color.argb, 0x33)
            canvas.drawPath(target, glowPaint)
            beamPaint.strokeWidth = 4f * density
            beamPaint.color = if (trace.status == BeamStatus.CROSSED) {
                withAlpha(trace.color.argb, 0x99)
            } else {
                trace.color.argb
            }
            canvas.drawPath(target, beamPaint)
        }
    }

    private fun drawConflicts(canvas: Canvas, radius: Float, size: Int) {
        conflicts.forEach { key ->
            val row = key / size
            val col = key % size
            val left = framePx + col * tile
            val top = framePx + row * tile
            cellRect.set(left + 2f, top + 2f, left + tile - 2f, top + tile - 2f)
            canvas.drawRoundRect(cellRect, radius, radius, conflictPaint)
        }
    }

    private fun withAlpha(color: Int, alpha: Int): Int = Color.argb(
        alpha,
        Color.red(color),
        Color.green(color),
        Color.blue(color)
    )

    private companion object {
        const val ROTATE_MS = 160L
    }
}
