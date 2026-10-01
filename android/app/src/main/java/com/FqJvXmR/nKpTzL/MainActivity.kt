package com.FqJvXmR.nKpTzL

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.FqJvXmR.nKpTzL.core.di.ServiceLocator
import com.FqJvXmR.nKpTzL.core.navigation.Navigator
import com.FqJvXmR.nKpTzL.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null

    val navigator: Navigator by lazy {
        Navigator(supportFragmentManager, R.id.fragment_container)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ServiceLocator.init(applicationContext)
        val inflated = ActivityMainBinding.inflate(layoutInflater)
        binding = inflated
        setContentView(inflated.root)
        if (savedInstanceState == null) {
            navigator.showSplash()
        }
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}
