package com.ceepilarsoubrier.systembarsdetection

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.ceepilarsoubrier.systembarsdetection.ui.theme.SystemBarsDetectionTheme

class MainActivity : ComponentActivity() {
    private var systemBarsVisible by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Enable immersive mode
        enableImmersiveMode()

        setContent {
            SystemBarsDetectionTheme {
                SystemBarsDetector(
                    onBarsVisibilityChanged = { visible ->
                        systemBarsVisible = visible
                    }
                )

                Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
                    StatusText(
                        barsVisible = systemBarsVisible,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        enableImmersiveMode()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            enableImmersiveMode()
        }
    }

    fun enableImmersiveMode() {
        window?.let { window ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.setDecorFitsSystemWindows(false)
                window.insetsController?.let { controller ->
                    controller.hide(WindowInsets.Type.systemBars())
                    controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_DEFAULT
                }
            } else {
                @Suppress("DEPRECATION")
                window.decorView.systemUiVisibility = (
                        View.SYSTEM_UI_FLAG_FULLSCREEN
                                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                or View.SYSTEM_UI_FLAG_IMMERSIVE
                                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        )
            }
        }
    }
}

@Composable
fun SystemBarsDetector(onBarsVisibilityChanged: (Boolean) -> Unit) {
    val context = LocalContext.current
    val hideHandler = remember { Handler(Looper.getMainLooper()) }

    DisposableEffect(Unit) {
        val activity = context as? MainActivity
        val window = activity?.window
        val decorView = window?.decorView

        val hideRunnable = Runnable {
            Log.d("SystemBars", "Auto-ocultando barras después de 5 segundos")
            activity?.enableImmersiveMode()
        }

        if (decorView != null) {
            @Suppress("DEPRECATION")
            val listener = View.OnSystemUiVisibilityChangeListener { visibility ->
                val barsVisible = (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN) == 0

                Log.d("SystemBars", "Barras ${if (barsVisible) "visibles" else "ocultas"}")
                onBarsVisibilityChanged(barsVisible)

                if (barsVisible) {
                    // Cancelar cualquier ocultación pendiente
                    hideHandler.removeCallbacks(hideRunnable)
                    // Programar nueva ocultación en 5 segundos
                    hideHandler.postDelayed(hideRunnable, 1000)
                }
            }

            decorView.setOnSystemUiVisibilityChangeListener(listener)

            onDispose {
                decorView.setOnSystemUiVisibilityChangeListener(null)
                hideHandler.removeCallbacks(hideRunnable)
            }
        }

        onDispose { }
    }
}

@Composable
fun StatusText(barsVisible: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (barsVisible) "Visible system bars" else "Hidden system bars",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}