package com.FqJvXmR.nKpTzL.core.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.FqJvXmR.nKpTzL.R
import com.FqJvXmR.nKpTzL.databinding.ViewSectionHeaderBinding

class SectionHeaderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding = ViewSectionHeaderBinding.inflate(LayoutInflater.from(context), this)

    init {
        orientation = VERTICAL
        val typed = context.obtainStyledAttributes(attrs, R.styleable.SectionHeaderView)
        val title = typed.getString(R.styleable.SectionHeaderView_headerTitle)
        val subtitle = typed.getString(R.styleable.SectionHeaderView_headerSubtitle)
        typed.recycle()
        if (title != null) {
            binding.headerTitle.text = title
        }
        if (subtitle != null) {
            binding.headerSubtitle.text = subtitle
        }
    }

    fun bind(title: CharSequence, subtitle: CharSequence) {
        binding.headerTitle.text = title
        binding.headerSubtitle.text = subtitle
    }

    fun setTitleSize(sizeSp: Float) {
        binding.headerTitle.textSize = sizeSp
    }
}
