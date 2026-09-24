package pt.tiagoduarte.challenge.form

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import pt.tiagoduarte.challenge.form.presentation.FormRoute
import pt.tiagoduarte.challenge.ui.theme.AppTheme

@AndroidEntryPoint
class FormActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                FormRoute()
            }
        }
    }
}
