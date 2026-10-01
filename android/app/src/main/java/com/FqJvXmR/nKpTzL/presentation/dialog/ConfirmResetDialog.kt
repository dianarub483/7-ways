package com.FqJvXmR.nKpTzL.presentation.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.FqJvXmR.nKpTzL.R
import com.FqJvXmR.nKpTzL.core.di.ServiceLocator
import com.FqJvXmR.nKpTzL.databinding.DialogConfirmBinding

class ConfirmResetDialog : DialogFragment() {

    private var _binding: DialogConfirmBinding? = null
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
        val inflated = DialogConfirmBinding.inflate(inflater, container, false)
        _binding = inflated
        return inflated.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.confirmNo?.setOnClickListener {
            dismissAllowingStateLoss()
        }
        binding?.confirmYes?.setOnClickListener {
            ServiceLocator.progressRepository().reset()
            dismissAllowingStateLoss()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
