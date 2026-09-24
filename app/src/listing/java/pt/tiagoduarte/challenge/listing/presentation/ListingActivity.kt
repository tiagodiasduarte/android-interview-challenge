package pt.tiagoduarte.challenge.listing.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import pt.tiagoduarte.challenge.listing.presentation.navigation.ListingNavGraph
import pt.tiagoduarte.challenge.ui.theme.AppTheme

@AndroidEntryPoint
class ListingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                ListingNavGraph(modifier = Modifier.fillMaxSize())
            }
        }
    }
}