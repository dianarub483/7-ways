package com.FqJvXmR.nKpTzL.core.ui

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.FqJvXmR.nKpTzL.R
import com.FqJvXmR.nKpTzL.databinding.ViewStatCardBinding

class StatCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding = ViewStatCardBinding.inflate(LayoutInflater.from(context), this)

    private var accentColor: Int = ContextCompat.getColor(context, R.color.gold_core)

    init {
        orientation = VERTICAL
        gravity = Gravity.CENTER_HORIZONTAL
        background = ContextCompat.getDrawable(context, R.drawable.shape_stat_card)
        val pad = resources.getDimensionPixelSize(R.dimen.stat_card_pad)
        setPadding(pad, pad, pad, pad)
        val typed = context.obtainStyledAttributes(attrs, R.styleable.StatCardView)
        val label = typed.getString(R.styleable.StatCardView_statLabel)
        val value = typed.getString(R.styleable.StatCardView_statValue)
        val accent = typed.getColor(R.styleable.StatCardView_statAccent, accentColor)
        val compact = typed.getBoolean(R.styleable.StatCardView_statCompact, false)
        typed.recycle()
        if (label != null) {
            binding.statLabel.text = label
        }
        binding.statValue.text = value ?: context.getString(R.string.stat_placeholder)
        setAccent(accent)
        if (compact) {
            binding.statValue.textSize = COMPACT_VALUE_SP
            binding.statLabel.textSize = COMPACT_LABEL_SP
        }
    }

    fun setAccent(color: Int) {
        accentColor = color
        binding.statAccent.backgroundTintList = android.content.res.ColorStateList.valueOf(color)
        binding.statValue.setTextColor(color)
    }

    fun bind(label: CharSequence, value: CharSequence) {
        binding.statLabel.text = label
        binding.statValue.text = value
        ViewCompat.setStateDescription(this, "$label $value")
    }

    fun setValueText(value: CharSequence) {
        binding.statValue.text = value
        ViewCompat.setStateDescription(this, "${binding.statLabel.text} $value")
    }

    private companion object {
        const val COMPACT_VALUE_SP = 18f
        const val COMPACT_LABEL_SP = 9f
    }
}
