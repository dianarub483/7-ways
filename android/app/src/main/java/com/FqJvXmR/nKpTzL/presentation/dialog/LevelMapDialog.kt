package com.FqJvXmR.nKpTzL.presentation.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.FqJvXmR.nKpTzL.MainActivity
import com.FqJvXmR.nKpTzL.R
import com.FqJvXmR.nKpTzL.core.di.ServiceLocator
import com.FqJvXmR.nKpTzL.data.sample.SampleData
import com.FqJvXmR.nKpTzL.databinding.DialogLevelMapBinding
import com.FqJvXmR.nKpTzL.domain.model.LevelSpec

class LevelMapDialog : DialogFragment() {

    private var _binding: DialogLevelMapBinding? = null
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.Theme_App7Ways_Dialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val inflated = DialogLevelMapBinding.inflate(inflater, container, false)
        _binding = inflated
        return inflated.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val views = binding ?: return
        val snapshot = ServiceLocator.progressRepository().snapshot()

        bindSet(views.mapSetOne, SampleData.specsForSet(SampleData.SET_BRONZE), snapshot.currentLevel, snapshot.stars)
        bindSet(views.mapSetTwo, SampleData.specsForSet(SampleData.SET_GOLDEN), snapshot.currentLevel, snapshot.stars)
        bindSet(views.mapSetThree, SampleData.specsForSet(SampleData.SET_CRIMSON), snapshot.currentLevel, snapshot.stars)

        views.mapClose.setOnClickListener {
            dismissAllowingStateLoss()
        }
    }

    private fun bindSet(
        list: RecyclerView,
        specs: List<LevelSpec>,
        unlockedThrough: Int,
        stars: Map<Int, Int>
    ) {
        list.layoutManager = LinearLayoutManager(list.context, RecyclerView.HORIZONTAL, false)
        list.adapter = LevelAdapter(specs, unlockedThrough, stars) { level ->
            launchLevel(level)
        }
    }

    private fun launchLevel(level: Int) {
        val host = activity as? MainActivity
        dismissAllowingStateLoss()
        host?.navigator?.restartGame(level)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        binding?.mapSetOne?.adapter = null
        binding?.mapSetTwo?.adapter = null
        binding?.mapSetThree?.adapter = null
        _binding = null
        super.onDestroyView()
    }
}
