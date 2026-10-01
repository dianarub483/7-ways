package com.FqJvXmR.nKpTzL.presentation.dialog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.FqJvXmR.nKpTzL.R
import com.FqJvXmR.nKpTzL.databinding.ItemLevelBinding
import com.FqJvXmR.nKpTzL.domain.model.LevelSpec

class LevelAdapter(
    private val specs: List<LevelSpec>,
    private val unlockedThrough: Int,
    private val stars: Map<Int, Int>,
    private val onPick: (Int) -> Unit
) : RecyclerView.Adapter<LevelAdapter.LevelViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LevelViewHolder {
        val binding = ItemLevelBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LevelViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LevelViewHolder, position: Int) {
        holder.bind(specs[position])
    }

    override fun getItemCount(): Int = specs.size

    inner class LevelViewHolder(private val binding: ItemLevelBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(spec: LevelSpec) {
            val earned = stars[spec.index] ?: 0
            val unlocked = spec.index <= unlockedThrough || earned > 0
            binding.levelNumber.text = spec.index.toString()
            binding.levelLock.visibility = if (unlocked) View.GONE else View.VISIBLE
            binding.levelStars.visibility = if (unlocked) View.VISIBLE else View.INVISIBLE
            binding.root.alpha = if (unlocked) 1f else LOCKED_ALPHA
            val icons = listOf(
                binding.levelStarOne,
                binding.levelStarTwo,
                binding.levelStarThree
            )
            icons.forEachIndexed { index, image ->
                image.setImageResource(
                    if (index < earned) R.drawable.ic_star else R.drawable.ic_star_outline
                )
            }
            val description = if (unlocked) {
                binding.root.context.getString(R.string.cd_level_stars_fmt, spec.index, earned)
            } else {
                binding.root.context.getString(R.string.cd_level_locked_fmt, spec.index)
            }
            binding.root.contentDescription = description
            ViewCompat.setStateDescription(binding.root, description)
            binding.root.isEnabled = unlocked
            binding.root.setOnClickListener {
                if (unlocked) {
                    onPick(spec.index)
                }
            }
        }
    }

    private companion object {
        const val LOCKED_ALPHA = 0.4f
    }
}
