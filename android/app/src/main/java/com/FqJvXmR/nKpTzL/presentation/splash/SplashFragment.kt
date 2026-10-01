package com.FqJvXmR.nKpTzL.presentation.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.FqJvXmR.nKpTzL.MainActivity
import com.FqJvXmR.nKpTzL.core.di.AppViewModelFactory
import com.FqJvXmR.nKpTzL.databinding.FragmentSplashBinding
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding

    private var animator: SplashAnimator? = null

    private val viewModel: SplashViewModel by viewModels { AppViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val inflated = FragmentSplashBinding.inflate(inflater, container, false)
        _binding = inflated
        return inflated.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val views = binding ?: return
        val rays = listOf(
            views.ray1,
            views.ray2,
            views.ray3,
            views.ray4,
            views.ray5,
            views.ray6,
            views.ray7
        )
        val created = SplashAnimator(
            emblem = views.splashEmblem,
            glow = views.splashGlow,
            title = views.splashTitle,
            subtitle = views.splashSubtitle,
            loadingLabel = views.splashLoading,
            rays = rays
        )
        animator = created
        created.prepare()
        created.start()
        ViewCompat.setStateDescription(views.splashProgress, views.splashLoading.text)
        observeState()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    val views = binding ?: return@collect
                    views.splashProgress.contentDescription =
                        getString(com.FqJvXmR.nKpTzL.R.string.splash_loading)
                    if (state.handOffReady) {
                        handOff()
                    }
                }
            }
        }
    }

    private fun handOff() {
        if (!isAdded) {
            return
        }
        val host = activity as? MainActivity ?: return
        if (!host.navigator.showMenu()) {
            return
        }
        viewModel.consumeHandOff()
    }

    override fun onDestroyView() {
        animator?.cancel()
        animator = null
        _binding = null
        super.onDestroyView()
    }
}
