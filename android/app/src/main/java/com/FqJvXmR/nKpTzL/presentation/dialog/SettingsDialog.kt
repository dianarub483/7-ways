package com.FqJvXmR.nKpTzL.presentation.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.fragment.app.DialogFragment
import com.FqJvXmR.nKpTzL.R
import com.FqJvXmR.nKpTzL.core.di.ServiceLocator
import com.FqJvXmR.nKpTzL.databinding.DialogSettingsBinding
import com.FqJvXmR.nKpTzL.domain.model.AppSettings

class SettingsDialog : DialogFragment() {

    private var _binding: DialogSettingsBinding? = null
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
        val inflated = DialogSettingsBinding.inflate(inflater, container, false)
        _binding = inflated
        return inflated.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val views = binding ?: return
        val repository = ServiceLocator.settingsRepository()
        val current = repository.settings()
        views.settingsSound.isChecked = current.soundCues
        views.settingsHaptics.isChecked = current.haptics
        views.settingsContrast.isChecked = current.highContrast
        describe(views.settingsSound, current.soundCues)
        describe(views.settingsHaptics, current.haptics)
        describe(views.settingsContrast, current.highContrast)

        val listener = View.OnClickListener {
            val updated = AppSettings(
                soundCues = views.settingsSound.isChecked,
                haptics = views.settingsHaptics.isChecked,
                highContrast = views.settingsContrast.isChecked
            )
            repository.update(updated)
            describe(views.settingsSound, updated.soundCues)
            describe(views.settingsHaptics, updated.haptics)
            describe(views.settingsContrast, updated.highContrast)
        }
        views.settingsSound.setOnClickListener(listener)
        views.settingsHaptics.setOnClickListener(listener)
        views.settingsContrast.setOnClickListener(listener)

        views.settingsReset.setOnClickListener {
            ConfirmResetDialog().show(parentFragmentManager, TAG_CONFIRM)
        }
        views.settingsClose.setOnClickListener {
            dismissAllowingStateLoss()
        }
    }

    private fun describe(target: View, enabled: Boolean) {
        ViewCompat.setStateDescription(
            target,
            getString(if (enabled) R.string.state_on else R.string.state_off)
        )
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

    private companion object {
        const val TAG_CONFIRM = "confirm_reset"
    }
}
